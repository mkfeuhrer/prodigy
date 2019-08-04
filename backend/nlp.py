import nltk
#nltk.download()
filename = 'abc.txt'
file = open(filename, 'rt')
text = file.read()
file.close()

from nltk import word_tokenize
from nltk.corpus import stopwords
import string
print(word_tokenize(text.lower()))
print()
stop = stopwords.words('english') + list(string.punctuation)
stop.append('``')
stop.append("''")
words = [i for i in word_tokenize(text.lower()) if i not in stop]

print(words)







"""
# convert to lower case
tokens = [w.lower() for w in tokens]
# remove punctuation from each word
import string
table = str.maketrans('', '', string.punctuation)
stripped = [w.translate(table) for w in tokens]
# remove remaining tokens that are not alphabetic
words = [word for word in stripped if word.isalpha()]
# filter out stop words
from nltk.corpus import stopwords
stop_words = set(stopwords.words('english'))
words = [w for w in words if not w in stop_words]
print(words)

from nltk.stem.porter import PorterStemmer
porter = PorterStemmer()
stemmed = [porter.stem(word) for word in tokens]
print(stemmed)

"""