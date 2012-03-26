from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.api import images
from google.appengine.api import users
from datamodel import Item
from datamodel import Picture
from datamodel import Thumbnail
import wsgiref.handlers
import logging
import simplejson

# TODO: ADD RPC SECURTIY STUFF (PRIVATE METHODS) on 
# http://code.google.com/appengine/articles/rpc.html 
class Image(webapp.RequestHandler):
  def get(self):      
      id = self.request.get('id')
      item = Item.get(id)
      type = self.request.get('get')
      
      if type == 'default_thumbnail':
        self.response.headers['Content-Type'] = 'image/jpeg'
        thumbnail = Thumbnail.all().ancestor(item).filter('index', item.thumbnail_index).get()
        if not thumbnail:
            self.redirect('/images/x.jpg')
        else:    
            self.response.out.write(thumbnail.picture)
        return  
      elif type == 'thumbnail':
        self.response.headers['Content-Type'] = 'image/jpeg'  
        n = int(self.request.get('n'))
        thumbnail = Thumbnail.all().ancestor(item).filter('index', n).get()
        if not thumbnail:
            self.redirect('/images/x.jpg')
        else:    
            self.response.out.write(thumbnail.picture)
        return 
      elif type == 'default':
        self.response.headers['Content-Type'] = 'image/jpeg'  
        n = int(self.request.get('n'))
        picture = Picture.all().ancestor(item).filter('index', n).get()
        if not picture:
            self.redirect('/images/x.jpg')
        else:    
            self.response.out.write(picture.picture)
        return
      elif type == 'description':
        n = int(self.request.get('n'))
        picture = Picture.all().ancestor(item).filter('index', n).get()
        if not picture:
            self.response.set_status(204)
        else:
            self.response.out.write(picture.description)
        return
      elif type == 'descriptions':
        pictures = Picture.all().ancestor(item).fetch(6)
        descriptions = {}
        for picture in pictures:
            descriptions[picture.index] = picture.description
        self.response.out.write(simplejson.dumps(descriptions))
        return
      else:
        self.error(404)
        return  
      # Either "id" wasn't provided, or there was no image with that ID
      # in the datastore.
      
      
  def post(self):
      item_key = db.Key(self.request.get('id'))
      item = Item.get(item_key)
      image = None
      if (self.request.get("img")):
          image = images.Image(self.request.get("img"))
      description = self.request.get("description")
      n = int(self.request.get('n'))
      write_image(item, image, n, description)
          
def write_image(item, image, n, description):
    #TODO What if item was got using item.put()
    if (n <= 5 and item and item.parent().user == users.get_current_user()):
      item_parts_to_write = []
      if (image):  
          if image.width > 580 or image.height > 400:
              image.resize(580, 400)
          image.im_feeling_lucky()    
          full_size = image.execute_transforms()
          image.resize(100, 100)
          thumb = image.execute_transforms()
      
          thumbnail = Thumbnail.all().ancestor(item).filter("index", n).get()
          if not thumbnail:
              thumbnail = Thumbnail(parent=item, index=n, picture=db.Blob(thumb))
              item.n_pictures += 1
          else:
              thumbnail.picture = db.Blob(thumb)
              thumbnail.index = n
          item_parts_to_write.append(thumbnail)
          item_parts_to_write.append(item)        
      
          picture = Picture.all().ancestor(item).filter("index", n).get()
          if not picture:
              picture = Picture(parent=item, index=n, picture=db.Blob(full_size))
          else:
              picture.picture = db.Blob(full_size)
              picture.index = n
          item_parts_to_write.append(picture)
          
          if (description):
              picture.description = description 
      
      #Only update description
      elif (description):
          picture = Picture.all().ancestor(item).filter("index", n).get()
          if picture:
              picture.description = description
              item_parts_to_write.append(picture)
              
      db.put(item_parts_to_write)
        
      
def main():
  application = webapp.WSGIApplication([('/image', Image)], debug=False)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()
