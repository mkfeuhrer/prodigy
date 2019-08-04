from flask import Flask
from flask import request
import json
from elasticsearch import Elasticsearch
# Connect to the elastic cluster
es=Elasticsearch([{'host':'localhost','port':9200}])
print(es)
app = Flask(__name__)
@app.route("/")
def root():
    return "Hello World!"
@app.route("/hello")
def hello():
    return "Hello World!"
# @app.route("/autocomplete/<string:query>/")
# def members(query):
#     return "Members"
@app.route("/search/<string:query>/")
def getMember(query):
    limit = request.args.get('limit')
    offset = request.args.get('offset')
    body={  
       "query":{  
          "multi_match":{  
             "query":query,
             "fields":[  
                "brand^2",
                "bundle_category^2",
                "bundle_subcategory^2",
                "color",
                "description",
                "source",
                "subcategory",
                "title^3"
             ],
             "fuzziness":2
          }
       }
    }
    res=es.search(index='',body=body,size=limit,from_=offset)
    hits=res["hits"]["hits"]
    # print (hits)
    default_thumbnail="http://www.netzerotools.com/assets/images/msa-10162695-workman-arc-flash-full-body-harness.png"
    default_title="Title not found"
    default_images=[]
    default_source="nil"
    default_country="others"
    default_stock=""
    default_url="nil"
    default_description="Not Found"
    default_bundle_subcategory="others"
    default_brand="others"
    default_source_type="ecommerce"
    default_subcategory="others"
    default_color="nil"
    default_mrp="0"
    default_base_product_url="nil"
    default_available_price="0"
    default_no_ratings="0"
    default_no_reviews="0"
    response=[]
    urls=set()
    for hit in hits:
        hit=hit["_source"]
        if ('url' in hit and hit['url'] in urls and hit['url'] != default_url):
            continue
        if not 'title' in hit:
            hit['title']=default_title
        if not 'images' in hit:
            hit['images']=default_images
        if not 'source' in hit:
            hit['source']=default_source
        if not 'country' in hit:
            hit['country']=default_country
        if not 'stock' in hit:
            hit['stock']=default_stock
        if not 'url' in hit:
            hit['url']=default_url
        if not 'description' in hit:
            hit['description']=default_description
        if not 'bundle_subcategory' in hit:
            hit['bundle_subcategory']=default_bundle_subcategory
        if not 'brand' in hit:
            hit['brand']=default_brand
        if not 'source_type' in hit:
            hit['source_type']=default_source_type
        if not 'subcategory' in hit:
            hit['subcategory']=default_subcategory
        if not 'color' in hit:
            hit['color']=default_color
        if not 'mrp' in hit:
            hit['mrp']=default_mrp
        if not 'base_product_url' in hit:
            hit['base_product_url']=default_base_product_url
        if not 'available_price' in hit:
            hit['available_price']=default_available_price
        if (not 'no_ratings' in hit) or not hit['no_ratings']:
            hit['no_ratings']=default_no_ratings
        if (not 'no_reviews' in hit) or not hit['no_reviews']:
            hit['no_reviews']=default_no_reviews
        response.append(hit)
        urls.add(hit['url'])
    return json.dumps(response)
if __name__ == "__main__":
    app.run(host="192.168.43.236")
