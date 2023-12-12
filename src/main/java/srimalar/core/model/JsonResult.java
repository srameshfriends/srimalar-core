
package srimalar.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
*
https://jsonapi.org/format/1.2/#fetching-sorting

Code	Problem
123	Value too short
225	Password lacks a letter, number, or punctuation character
226	Passwords do not match
227	Password cannot be one of last five passwords

HTTP/1.1 422 Unprocessable Entity
Content-Type: application/vnd.api+json

{
  "jsonapi": { "version": "1.0" },
  "errors": [
    {
      "code":   "123",
      "source": { "pointer": "/data/attributes/firstName" },
      "title":  "Value is too short",
      "detail": "First name must contain at least two characters."
    },
    {
      "code":   "225",
      "source": { "pointer": "/data/attributes/password" },
      "title": "Passwords must contain a letter, number, and punctuation character.",
      "detail": "The password provided is missing a punctuation character."
    },
    {
      "code":   "226",
      "source": { "pointer": "/data/attributes/password" },
      "title": "Password and password confirmation do not match."
    }
  ]
}

* {
  "errors": [
    {
      "status": "403",
      "source": { "pointer": "/data/attributes/secretPowers" },
      "detail": "Editing secret powers is not authorized on Sundays."
    },
    {
      "status": "422",
      "source": { "pointer": "/data/attributes/volume" },
      "detail": "Volume does not, in fact, go to 11."
    },
    {
      "status": "500",
      "source": { "pointer": "/data/attributes/reputation" },
      "title": "The backend responded with an error",
      "detail": "Reputation service not responding after three requests."
    }
  ]
}
*
* GET /articles?include=author HTTP/1.1
HTTP/1.1 200 OK
Content-Type: application/vnd.api+json
{
  "data": [{
    "type": "articles",
    "id": "1",
    "attributes": {
      "title": "JSON:API paints my bikeshed!",
      "body": "The shortest article. Ever.",
      "created": "2015-05-22T14:56:29.000Z",
      "updated": "2015-05-22T14:56:28.000Z"
    },
    "relationships": {
      "author": {
        "data": {"id": "42", "type": "people"}
      }
    }
  }],
  "included": [
    {
      "type": "people",
      "id": "42",
      "attributes": {
        "name": "John",
        "age": 80,
        "gender": "male"
      }
    }
  ]
}
*
*
GET /articles?page[number]=3&page[size]=1 HTTP/1.1
*
* HTTP/1.1 200 OK
Content-Type: application/vnd.api+json

{
  "meta": {
    "totalPages": 13
  },
  "data": [
    {
      "type": "articles",
      "id": "3",
      "attributes": {
        "title": "JSON:API paints my bikeshed!",
        "body": "The shortest article. Ever.",
        "created": "2015-05-22T14:56:29.000Z",
        "updated": "2015-05-22T14:56:28.000Z"
      }
    }
  ],
  "links": {
    "self": "http://example.com/articles?page[number]=3&page[size]=1",
    "first": "http://example.com/articles?page[number]=1&page[size]=1",
    "prev": "http://example.com/articles?page[number]=2&page[size]=1",
    "next": "http://example.com/articles?page[number]=4&page[size]=1",
    "last": "http://example.com/articles?page[number]=13&page[size]=1"
  }
}
*
*
* Conventions
The key words “MUST”, “MUST NOT”, “REQUIRED”, “SHALL”, “SHALL NOT”, “SHOULD”, “SHOULD NOT”, “RECOMMENDED”, “NOT RECOMMENDED”, “MAY”, and “OPTIONAL” in this document are to be interpreted as described in BCP 14 [RFC2119] [RFC8174] when, and only when, they appear in all capitals, as shown here.
* https://jsonapi.org/format/1.2/#fetching-sorting
* */

public class JsonResult {

    @JsonProperty("status")
    private int status;

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("message")
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
