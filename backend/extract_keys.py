import json

keys = set()
f = open("./fashion/20190801_20190801_1_kohls-us_product", "r")
while True:
    data_json = f.readline()
    if not data_json:
        break
    data_json = json.loads(data_json)
    keys.update(data_json.keys())

print(keys)