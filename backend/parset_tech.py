import os
import json
fields = ['color', 'no_ratings', 'thumbnail', 'seed_price', 'mrp', 'warranty', 'url', 'inthebox', 'page', 'no_reviews', 'title', 'description', 'seed_url', 'available_price', 'features', 'images', 'subcategory', 'stock', 'images_temp', 'product_features', 'title_temp', 'category', 'specification']
file_list = [20190801_20190801_1_bestbuy-us_product]
output_file = open('output_tech.json','w')
json_array = []
for file in file_list:
  print('processing: ' + file)
  f = open(file, "r")
  while True:
    line = {}
    data_json = f.readline()
    if not data_json:
      break
    data_json = json.loads(data_json)
    for field in fields:
      if data_json.get(field) != None:
        line[field] = data_json.get(field)
    json_array.append(line)
json.dump(json_array,output_file)