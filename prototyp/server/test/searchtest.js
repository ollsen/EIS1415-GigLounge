var assert = require('assert');
var should = require('should');
var request = require('supertest');
var app = require('../app');
var db = require('../db');
var Admin = db.model('Admin');
var User = db.model('User');
var useragent = request.agent(app);

var usersjson = require('./samples/users.json');


describe('/search without login', function() {
    it('should respond with message "Please login"', function(done) {
        useragent
            .get('/search')
            .expect(404, done);
    });
});

describe('/search with login', function() {
    var userId;
    var sessionId;
    var searchId;
    before(function(done){
        
        usersjson.users.forEach(function(user){
            useragent
                .post('/signup')
                .send(user)
                .expect(200)
                .end(function(err, res){
                    User.findOne({email: user.username}, function(err ,dbuser){
                        dbuser.country = user.country;
                        dbuser.city = user.city;
                        dbuser.postcode = user.postcode;
                        dbuser.free = user.free;
                        dbuser.save(function(err){
                            if(err)
                                console.log(err);
                        });
                    });
                    if(user.username === 'mail5@example.biz') {
                        useragent
                            .post('/login')
                            .send({ username: usersjson.users[0].username,
                                    password: usersjson.users[0].password
                                })
                            .expect(200)
                            .end(function(err, res){
                                useragent
                                    .get('/users')
                                    .expect(200)
                                    .end(function(err, res){
                                        sessionId = res.body.users[0]._id;
                                        userId = res.body.users[1]._id;
                                        done();
                                });
                                //done();
                        });
                    }
                });
        });
    });
    
    it('should add search', function(done){
        useragent
            .post('/search')
            .send({ instrument: 'guitarist',
                    country: 'DE',
                    city: 'Cologne',
                    postcode: '51063',
                    radius: 10
                  })
            .expect(200)
            .end(function(err, res){
                res.body.message.should.equal('Search added');
                searchId = res.body.id;
                done();
            });
    });
    
    it('should receive search results', function(done) {
        useragent
            .get('/search/'+searchId)
            .expect(200)
            .end(function(err, res){
                //res.body.message.should.equal('No Events');
                console.log(res.body);
                done();
        });
    });
    
    
    
    after(function(done){
        var db = require('../db');
        var User = db.model('User');
        usersjson.users.forEach(function(user){
            User.remove({email: user.username}, function(err){
                if(err)
                    console.log(err);
                else
                    console.log('user removed');
            });
        });
        var Band = db.model('Band');
        Band.remove({name: 'Another Band'}, function(err){
            if(err)
                    console.log(err);
                else
                    console.log('band removed');
        })
        done();
    });
});