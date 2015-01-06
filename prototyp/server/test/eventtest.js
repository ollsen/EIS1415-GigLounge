var assert = require('assert');
var should = require('should');
var request = require('supertest');
var app = require('../app');
var db = require('../db');
var Admin = db.model('Admin');
var User = db.model('User');
var useragent = request.agent(app);
var Band = db.model('Band');
var Event = db.model('Event');

var usersjson = require('./samples/users.json');


describe('/events without login', function() {
    it('should respond with message "Please login"', function(done) {
        useragent
            .get('/events')
            .expect(200)
            .end(function(err, res){
                res.body.message.should.equal('Please login');
                done();
        });
    });
});

describe('/events with login', function() {
    var userId;
    var sessionId;
    var bandId;
    var eventId;
    before(function(done){
        
        usersjson.users.forEach(function(user){
            useragent
                .post('/signup')
                .send(user)
                .expect(200)
                .end(function(err, res){
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
                                        useragent
                                            .post('/bands')
                                            .send({ name: 'Another Band',
                                                    country: 'Germany',
                                                    city: 'Cologne',
                                                    postcode: '51063',
                                                    members: {role: 'guitarist'}
                                                })
                                            .expect(200)
                                            .end(function(err, res){
                                                console.log(res.body);
                                                res.body.message.should.equal('Band created');
                                                bandId = res.body.id;
                                                Band.findById(bandId, function(err, qband){
                                                    qband.members.push({ user: userId,
                                                                        role: usersjson.users[1].instrument[0]
                                                                    });
                                                    qband.members.push({ user: userId,
                                                                        role: usersjson.users[2].instrument[0]
                                                                    });
                                                    qband.members.push({ user: userId,
                                                                        role: usersjson.users[4].instrument[0]
                                                                    });
                                                    qband.save(function(err){
                                                        if(err)
                                                            console.log(err);
                                                        else
                                                            console.log('member added');
                                                            done();
                                                    });
                                                });
                                        });
                                });
                                //done();
                        });
                    }
                });
        });
    });
    
    it('should receive json message "No Events"', function(done) {
        useragent
            .get('/events')
            .expect(200)
            .end(function(err, res){
                console.log(res.body);
                res.body.message.should.equal('No Events');
                done();
        });
    });
    
    it('should add event', function(done){
        useragent
            .post('/events')
            .send({
                    name: 'Event 1',
                    location: 'Location',
                    country: 'DE',
                    city: 'Cologne',   
                    postcode: '50667',
                    adress: 'Hohestrasse 100'
            })
            .end(function(err ,res){
                console.log(res.body);
                res.body.message.should.equal('Event created');
                eventId = res.body.id;
                done();
        });
    });
    
    it('should add lineup to event', function(done){
        Band.findById(bandId, function(err, band){
            useragent
                .post('/events/'+eventId+'/lineup')
                .send({
                    band: bandId,
                    lineup: [{  user : band.members[1].user,
                                role : band.members[1].role
                            },{  user : band.members[2].user,
                                role : band.members[2].role
                            },{  user : band.members[3].user,
                                role : band.members[3].role
                            },{
                                role: band.members[2].role
                            }]
                })
                .end(function(err, res){
                    res.body.message.should.equal('Band added');
                    done();
            }); 
        });
        
    });
    
    
    after(function(done){
        var db = require('../db');
        usersjson.users.forEach(function(user){
            User.remove({email: user.username}, function(err){
                if(err)
                    console.log(err);
                else
                    console.log('user removed');
            });
        });
        Band.remove({name: 'Another Band'}, function(err){
            if(err)
                    console.log(err);
                else
                    console.log('band removed');
        });
        Event.remove({name: 'Event 1'}, function(err){
            if(err)
                    console.log(err);
                else
                    console.log('event removed');
        });
        
        done();
    });
});