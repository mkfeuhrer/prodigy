import os
import json
fields = ['images', 'source', 'final_url', 'thumbnail', 'bundle_category', 'country', 'stock', 'seed_url', 'url', 'mrp',
       'size', 'available_price', 'description', 'bundle_subcategory', 'no_ratings', 'brand', 'source_type', 'upc',
       'no_reviews', 'title', 'subcategory', 'color', 'base_product_url'
       ]
f = open("./fashion/20190801_20190801_1_bloomingdales-us_product", "r")    
output_file = open('output.csv','wb')
line = ""
for field in fields:
    line += field + "##"
line += "\n"
output_file.write(line.encode("UTF-8"))
while True:
	line = ""
	data_json = f.readline()
	if not data_json:
		break
	data_json = json.loads(data_json)
	for field in fields:
		if data_json.get(field) != None:
			line += data_json.get(field) + '##'
		else:
			line += 'not_available'+'##'
	line += '\n'
	output_file.write(line.encode('UTF-8'))