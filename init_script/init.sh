echo "Downloading tullave"
curl -s http://mapas.tullaveplus.com/recarga | grep charge_json | cut -d '"' -f2 > links.txt
curl -s http://mapas.tullaveplus.com`sed -n '1p' links.txt` | sed 's/.\{2\}$//' | sed 's/^.\{10\}//' > data1.json
curl -s http://mapas.tullaveplus.com`sed -n '2p' links.txt` | sed 's/.\{2\}$//' | sed 's/^.\{10\}//' > data2.json

echo "Preparing dump"

cat data1.json | jq -r ".rows[] | { name: .row.name , address: .row.add, latitude: .row.geojson.coordinates[1], longitude: .row.geojson.coordinates[0], horario_week: .row.wks, horario_sabado: .row.exs, horario_domingo_festivo: .row.hds} | @text \"INSERT INTO puntos_recargas(name, address, position, horario_week, horario_sabado, horario_domingo_festivo) VALUES('\(.name)', '\(.address)', ST_GeographyFromText('Point(\(.longitude) \(.latitude))'), '\(.horario_week', '\(.horario_sabado)', '\(.horario_domingo_festivo)');\" " > dump_tullave.sql
cat data2.json | jq -r ".rows[] | { name: .row.name , address: .row.add, latitude: .row.geojson.coordinates[1], longitude: .row.geojson.coordinates[0], horario_week: .row.wks, horario_sabado: .row.exs, horario_domingo_festivo: .row.hds} | @text \"INSERT INTO puntos_recargas(name, address, position, horario_week, horario_sabado, horario_domingo_festivo) VALUES('\(.name)', '\(.address)', ST_GeographyFromText('Point(\(.longitude) \(.latitude))'), '\(.horario_week)', '\(.horario_sabado)', '\(.horario_domingo_festivo)');\" " >> dump_tullave.sql
sed -i 's/null/00:00/g' dump_tullave.sql

echo "Please put transmitp.apk"

read

echo "Extracting data"

unzip -q transmitp.apk -d transmitp

echo "Preparing DB"

dropdb easitp
createdb -O easitp easitp

psql easitp -c "CREATE EXTENSION postgis"
psql easitp -c "CREATE EXTENSION postgis_topology"
psql easitp -c "CREATE EXTENSION postgis_sfcgal"
psql easitp -c "CREATE EXTENSION fuzzystrmatch"
psql easitp -c "CREATE EXTENSION postgis_tiger_geocoder"

psql easitp -c "CREATE TABLE puntos_recargas (name character varying(150), address character varying(150), position geography, horario_week character varying(50), horario_sabado character varying(50), horario_domingo_festivo character varying(50))"
psql easitp -c "ALTER TABLE puntos_recargas OWNER TO easitp;"
psql -q easitp < dump_tullave.sql


psql easitp -c "CREATE TABLE buses (id integer NOT NULL, line character varying(30), name character varying(150), station character varying(150), address character varying(150), position geography)"
psql easitp -c "ALTER TABLE buses OWNER TO easitp;"
sqlite3 transmitp/assets/transmi_sitp "select b.pk_id, b.name, b.description, e.name, e.description, e.latitud, e.longitud from estacion e left join bus_estacion be on be.fk_estacion = e.pk_id join bus b on b.pk_id = be.fk_bus where b.fk_agency = 'SITP-U' order by b.name, b.description" | awk -F "|" '{ print "INSERT INTO buses(id, line, name, station, address, position) values("$1",'\''"$2"'\'', '\''"$3"'\'', '\''"$4"'\'', '\''"$5"'\'',  ST_GeographyFromText('\''POINT("$7" "$6")'\''));" }' | psql -q easitp

psql easitp -c "CREATE TABLE days (id integer NOT NULL, short character varying(5), "long" character varying(30), CONSTRAINT day_id_pk PRIMARY KEY (id))"
psql easitp -c "ALTER TABLE days OWNER TO easitp;"
sqlite3 transmitp/assets/transmi_sitp 'select * from dia' | awk -F "|" '{print "INSERT INTO days(id, short, long) VALUES("$1",'\''"$2"'\'','\''"$3"'\'');"}' | psql -q easitp

psql easitp -c "CREATE TABLE bus_horario ( bus_id integer, day_id integer, desde character varying(20), hasta character varying(20), CONSTRAINT day_fk FOREIGN KEY (day_id) REFERENCES days (id) MATCH SIMPLE)"
psql easitp -c "ALTER TABLE bus_horario OWNER TO easitp;"
sqlite3 transmitp/assets/transmi_sitp 'select b.pk_id, bh.fk_dia, bh.desde, bh.hasta from bus b left join bus_horario bh on b.pk_id = bh.fk_bus where b.fk_agency = "SITP-U"' | awk -F "|" '{ print "INSERT INTO bus_horario(bus_id, day_id, desde, hasta) VALUES("$1","$2",'\''"$3"'\'','\''"$4"'\'');" }' | psql -q easitp

psql easitp -c "CREATE TABLE festivos (day date)"
psql easitp -c "ALTER TABLE festivos OWNER TO easitp;"
sqlite3 transmitp/assets/transmi_sitp 'select pk_id from festivo' | awk -F "|" '{ print "INSERT INTO festivos(day) VALUES('\''"$1"'\''::date);" }' | psql -q easitp

psql easitp -c "update puntos_recargas set horario_week = '00:00-00:00' where horario_week = '';"
psql easitp -c "update puntos_recargas set horario_sabado = '00:00-00:00' where horario_sabado = '';"
psql easitp -c "update puntos_recargas set horario_domingo_festivo = '00:00-00:00' where horario_domingo_festivo = '';"

psql easitp -c "insert into puntos_recargas select name, address, position, regexp_split_to_table(horario_week, ' (Y|y) ') as horario_week, '00:00-00:00' as horario_sabado, '00:00-00:00' as horario_domingo_festivo from puntos_recargas where upper(horario_week) like '% Y %';"
psql easitp -c "delete from puntos_recargas where upper(horario_week) like '% Y %';"

psql easitp -c "insert into puntos_recargas select name, address, position, '00:00-00:00' as horario_week, regexp_split_to_table(horario_sabado, ' (Y|y) ') as horario_sabado, '00:00-00:00' as horario_domingo_festivo from puntos_recargas where upper(horario_sabado) like '% Y %';"
psql easitp -c "delete from puntos_recargas where upper(horario_sabado) like '% Y %';"

psql easitp -c "insert into puntos_recargas select name, address, position, '00:00-00:00' as horario_week, '00:00-00:00' as horario_sabado, regexp_split_to_table(horario_domingo_festivo, ' (Y|y) ') as horario_domingo_festivo from puntos_recargas where upper(horario_domingo_festivo) like '% Y %';"
psql easitp -c "delete from puntos_recargas where upper(horario_domingo_festivo) like '% Y %';"

echo "Clean up"

rm data1.json
rm data2.json
rm dump_tullave.sql 
rm links.txt 
rm -rf transmitp
rm transmitp.apk
