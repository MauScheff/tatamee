import logging
import datetime
from google.appengine.ext import db
from django.utils import simplejson

class CountryCounter(db.Model):
  country = db.StringProperty()
  users = db.IntegerProperty()

#Change to Region and use Google GeoCoding Region and Country Code
class CityCounter(db.Model):
  city = db.StringProperty()  
  users = db.IntegerProperty()

class User(db.Model):
  user = db.UserProperty()
  timestamp = db.DateTimeProperty(auto_now_add=True)
  home = db.GeoPtProperty(required=True)
  phone = db.PhoneNumberProperty()
  locale = db.StringProperty()
  username = db.StringProperty(default='')
  currency = db.StringProperty()
  area = db.StringProperty(required=True)
  categories = db.StringListProperty()
  good_ratings = db.IntegerProperty(default=0)
  bad_ratings = db.IntegerProperty(default=0)
  
class Comment(db.Model):
  date = db.DateTimeProperty(auto_now_add=True)
  message = db.TextProperty(required=True)
  poster = db.StringProperty(required=True)

class Item(db.Model):
  title = db.StringProperty()
  currency = db.StringProperty()
  category = db.StringProperty()
  tags = db.TextProperty()
  description = db.TextProperty()#Not Indexed
  timestamp = db.DateTimeProperty(auto_now_add=True)
  price = db.FloatProperty()
  contact = db.StringProperty()
  thumbnail_index = db.IntegerProperty(default=0)
  n_pictures = db.IntegerProperty(default=0)
  features = db.StringListProperty()
  values = db.StringListProperty()
  location = db.GeoPtProperty()
  comments = db.IntegerProperty(default=0)
  
class ItemLight(db.Model):
  item_key = db.ReferenceProperty(Item, required=True)
  area = db.StringProperty(required=True)
  location = db.GeoPtProperty(required=True)
  tags = db.StringListProperty()
  extra_tags = db.StringListProperty()
  title = db.StringProperty()
  
class Picture(db.Model):
  index = db.IntegerProperty(required=True)
  picture = db.BlobProperty(required=True)
  description = db.TextProperty(default='')
  
class Thumbnail(db.Model):
  index = db.IntegerProperty(required=True)
  picture = db.BlobProperty(required=True)

class GhostRating(db.Model):
  date = db.DateTimeProperty(auto_now_add=True)
  id = db.StringProperty()
  email = db.EmailProperty()

class GoodRating(db.Model):
  date = db.DateTimeProperty(auto_now_add=True)
  message = db.TextProperty()
  email = db.EmailProperty()
  
class BadRating(db.Model):
  date = db.DateTimeProperty(auto_now_add=True)
  message = db.TextProperty()    
  email = db.EmailProperty()
  report = db.IntegerProperty(default=0)
    
class MyEncoder(simplejson.JSONEncoder):
  def default(self, o):
    try:
        if (type(o) == Item):
            delta = datetime.datetime.now() - o.timestamp
            days_remaining = str(30 - delta.days)
            user = o.parent()
            return { 'title' : o.title, 'price': o.price,
            'id': str(o.key()), 'description': o.description,
            'days_remaining': days_remaining, 'n_pictures':o.n_pictures,
            'comments': str(o.comments), 'seller': user.user.nickname(),
            'bad_ratings': user.bad_ratings, 'good_ratings': user.good_ratings,
            'nickname': user.user.nickname(), 'user_id': user.key().name()[4:], 
            'lat': o.location.lat, 'lon': o.location.lon, 'tags': o.tags, 
            'category': o.category, 'currency': o.currency }
        elif (type(o) == ItemLight):
            return { 'title' : o.title, 'id': str(o.item_key.key()),
                     'lat': o.location.lat, 'lon': o.location.lon}
        elif (type(o) == Comment):
            #TODO: Obfuscate poster email
            return { 'poster' : o.poster, 'message': o.message, 'date': str(o.date.date())}
        elif (type(o) == GoodRating or type(o) == BadRating):
            return { 'poster' : o.email, 'message': o.message, 'date': str(o.date.date())}
        
    except TypeError:
        pass
    return JSONEncoder.default(self, o)