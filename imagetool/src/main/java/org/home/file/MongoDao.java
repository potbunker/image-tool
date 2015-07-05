package org.home.file;

import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;



public class MongoDao {

  private final MongoClient client = new MongoClient(new ServerAddress(new InetSocketAddress("xubuntu0", 27017)));
  private final DBCollection coll = client.getDB("pictures").getCollection("summary");

  public MongoDao() {
    super();
  }
  
  public final void save(DBObject x) {
    DBCollection collection = this.coll;
    collection.insert(x);
  };
  
  public final Function<PictureFile, DBObject> convert = x -> {

    return BasicDBObjectBuilder.start()
        .add("name",  x.path.toString())
        .add("md5", x.md5)
        .add("size", x.size)
        .get();
  };
}
