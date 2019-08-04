"""import os
import json
fields = ['images', 'source', 'final_url', 'thumbnail', 'bundle_category', 'country', 'stock', 'seed_url', 'url', 'mrp',
        'size', 'available_price', 'description', 'bundle_subcategory', 'no_ratings', 'brand', 'source_type', 'upc',
        'no_reviews', 'title', 'subcategory', 'color', 'base_product_url'
        ]
file_list = ["20190801_20190801_1_bloomingdales-us_product", "20190801_20190801_1_kohls-us_product", "20190801_20190801_1_macys-us_product", "20190801_20190801_1_saksfifthavenue-us_product", "20190801_20190801_1_zappos-us_product", "20190802_20190801_1_bloomingdales-us_product", "20190802_20190801_1_kohls-us_product", "20190802_20190801_1_macys-us_product", "20190802_20190801_1_saksfifthavenue-us_product", "20190802_20190801_1_zappos-us_product", "20190802_20190802_1_bloomingdales-us_product", "20190802_20190802_1_kohls-us_product", "20190802_20190802_1_macys-us_product", "20190802_20190802_1_saksfifthavenue-us_product", "20190802_20190802_1_zappos-us_product", "20190803_20190803_1_zappos-us_product"]
output_file = open('output.json','w')
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
"""



import os
import json

import nltk
from nltk import word_tokenize
from nltk.corpus import stopwords
import string



fields = ['images', 'source', 'final_url', 'thumbnail', 'bundle_category', 'country', 'stock', 'seed_url', 'url', 'mrp',
        'size', 'available_price', 'description', 'bundle_subcategory', 'no_ratings', 'brand', 'source_type', 'upc',
        'no_reviews', 'title', 'subcategory', 'color', 'base_product_url'
        ]
file_list = ["20190801_20190801_1_bloomingdales-us_product", "20190801_20190801_1_kohls-us_product", "20190801_20190801_1_macys-us_product", "20190801_20190801_1_saksfifthavenue-us_product", "20190801_20190801_1_zappos-us_product", "20190802_20190801_1_bloomingdales-us_product", "20190802_20190801_1_kohls-us_product", "20190802_20190801_1_macys-us_product", "20190802_20190801_1_saksfifthavenue-us_product", "20190802_20190801_1_zappos-us_product", "20190802_20190802_1_bloomingdales-us_product", "20190802_20190802_1_kohls-us_product", "20190802_20190802_1_macys-us_product", "20190802_20190802_1_saksfifthavenue-us_product", "20190802_20190802_1_zappos-us_product", "20190803_20190803_1_zappos-us_product"]
output_file = open('output.json','w')

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
        if field in ['bundle_subcategory','bundle_category','description','title','subcategory']:
          text = data_json.get(field)
          #text = "iphone 6, iphone 7, iphone 8"
          stop = stopwords.words('english') + list(string.punctuation)
          stop.append('``')
          stop.append("''")
          words = [i for i in word_tokenize(text.lower()) if i not in stop]
          st = ''
          key = set()
          for word in words:
            key.add(word)
          for item in key:
            st += item + " "
          if st != '':
            #print(st)
            line[field] = st
        else: 
          line[field] = data_json.get(field)
    json_array.append(line)
json.dump(json_array,output_file)