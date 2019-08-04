from torchvision import models
import torch
import numpy as np
import urllib
import cv2
from PIL import Image
import requests
from io import BytesIO

def url_to_image(url):
	response = requests.get(url)
	img = Image.open(BytesIO(response.content))
	return img
 
# print(dir(models))

alexnet = models.alexnet(pretrained=True)

# print(alexnet)

from torchvision import transforms
transform = transforms.Compose([             #[1]
  transforms.Resize(256),                    #[2]
  transforms.CenterCrop(224),                #[3]
  transforms.ToTensor(),                     #[4]
  transforms.Normalize(                      #[5]
  mean=[0.485, 0.456, 0.406],                #[6]
  std=[0.229, 0.224, 0.225]                  #[7]
)])


img = url_to_image("https://media.kohlsimg.com/is/image/kohls/3628162_Portland_Wash/")

img_t = transform(img)
batch_t = torch.unsqueeze(img_t, 0)

# Carry out inference
alexnet.eval()
out = alexnet(batch_t)
print(out.shape)

# Load labels
with open('imagenet_classes.txt') as f:
  labels = [line.strip() for line in f.readlines()]

# Forth, print the top 5 classes predicted by the model
_, indices = torch.sort(out, descending=True)
percentage = torch.nn.functional.softmax(out, dim=1)[0] * 100
print([(labels[idx], percentage[idx].item()) for idx in indices[0][:5]])