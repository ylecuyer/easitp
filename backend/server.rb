require 'sinatra'
require 'pg'
require 'json'
require 'connection_pool'

configure do
  pg = ConnectionPool.new(size: 2) {
    PG.connect(dbname: 'easitp', user: 'easitp', password: 'easitp')
  }	

  set :pg, pg
end

def parse(results)

  data = []

  fields = results.fields
  results.each do |row|
    data << Hash[fields.map{|x| [x, row[x]]}]
  end

  data
end

get '/line/:id' do
  content_type :json
  
  id = params[:id]

  line_query = %{
    select ST_X(position::geometry) as longitude, ST_Y(position::geometry) as latitude, address, station from buses where id = #{id}
  }

  results = settings.pg.with do |conn|
    conn.exec line_query
  end

  parse(results).to_json
end

get '/sitp/:lat_from/:lon_from/:distance_from/:lat_to/:lon_to/:distance_to' do
  content_type :json

  lat_from = params[:lat_from]
  lon_from = params[:lon_from]
  distance_from = params[:distance_from]
  lat_to = params[:lat_to]
  lon_to = params[:lon_to]
  distance_to = params[:distance_to]
  
  sitp_query = %{
    select b.id, b.line, trim(both from split_part(b.name, '»', 2)) as destino, d.short, bh.desde, bh.hasta from (
      select id, line, name from buses where ST_Distance(ST_GeographyFromText('Point(#{lon_from} #{lat_from})'),position) <= #{distance_from}
      intersect
      select id, line, name from buses where ST_Distance(ST_GeographyFromText('Point(#{lon_to} #{lat_to})'),position) <= #{distance_to}
    ) b
    left join bus_horario bh on bh.bus_id = b.id
    left join days d on d.id = bh.day_id
    where CURRENT_TIME BETWEEN desde::TIME and hasta::TIME
    and case 
    case when exists (select day from festivos where CURRENT_DATE = day) then 0 else extract(dow FROM CURRENT_DATE) end
    when 0 then bh.day_id IN (4, 5)
    when 1 then bh.day_id IN (1, 2, 5)
    when 2 then bh.day_id IN (1, 2, 5)
    when 3 then bh.day_id IN (1, 2, 5)
    when 4 then bh.day_id IN (1, 2, 5)
    when 5 then bh.day_id IN (1, 2, 5)
    when 6 then bh.day_id IN (2, 3, 5)
    end
    order by line
  }

  results = settings.pg.with do |conn|
    conn.exec sitp_query
  end

  parse(results).to_json
end

get '/tullave/:lat/:lon/:distance' do
  content_type :json

  lat = params[:lat]
  lon = params[:lon]
  distance = params[:distance]
  
  tullave_query = %{
    select ST_X(pr.position::geometry) as longitude, ST_Y(pr.position::geometry) as latitude, pr.name, pr.address, pr.horario_week, pr.horario_sabado, pr.horario_domingo_festivo
    from puntos_recargas pr 
    where ST_Distance(ST_GeographyFromText('Point(#{lon} #{lat})'), position) <= #{distance}
    and case 
    case when exists (select day from festivos where CURRENT_DATE = day) then 0 else extract(dow FROM CURRENT_DATE) end
    when 0 then CURRENT_TIME BETWEEN split_part(pr.horario_domingo_festivo, '-', 1)::time and split_part(pr.horario_domingo_festivo, '-', 2)::time
    when 1 then CURRENT_TIME BETWEEN split_part(pr.horario_week, '-', 1)::time and split_part(pr.horario_week, '-', 2)::time
    when 2 then CURRENT_TIME BETWEEN split_part(pr.horario_week, '-', 1)::time and split_part(pr.horario_week, '-', 2)::time
    when 3 then CURRENT_TIME BETWEEN split_part(pr.horario_week, '-', 1)::time and split_part(pr.horario_week, '-', 2)::time
    when 4 then CURRENT_TIME BETWEEN split_part(pr.horario_week, '-', 1)::time and split_part(pr.horario_week, '-', 2)::time
    when 5 then CURRENT_TIME BETWEEN split_part(pr.horario_week, '-', 1)::time and split_part(pr.horario_week, '-', 2)::time
    when 6 then CURRENT_TIME BETWEEN split_part(pr.horario_sabado, '-', 1)::time and split_part(pr.horario_sabado, '-', 2)::time
    end
  }
  
  results = settings.pg.with do |conn|
    conn.exec tullave_query
  end

  parse(results).to_json
end
