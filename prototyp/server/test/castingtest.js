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
var Gig = db.model('Gig');
var Search = db.model('Search');
var Casting = db.model('Casting');

var usamples = require('./samples/users.json');

/*
* if test fails then restart
*/

describe('server available check', function(){
    it('should show status online', function(done){
        useragent
            .get('/')
            .set('Accept', 'application/json')
            .expect(200)
            .end(function(err, res){
                res.body.message.should.equal('ok');
                done();
        });
    });
});

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

describe('casting', function(){
    var sessionId;
    var userId;
    var bandId;
    var eventId;
    var gigId;
    var csId;
    var commentId;
    var mediaId;
    describe('signin', function(){
        it('should register all users', function(done){
            usamples.users.forEach(function(user){
                useragent
                    .post('/signup')
                    .send(user)
                    .end(function(err, res){
                        if(user.username === 'mail5@example.biz')
                            done();
                });
            });
        });
        it('should login', function(done){
            useragent
                .post('/login')
                .send({ username: usamples.users[4].username,
                        password: usamples.users[4].password
                        })
                .expect(200, done);
        });
    });
    
    describe('edit userprofiles', function(){
        it('should all profiles', function(done){
            usamples.users.forEach(function(user){
                User.findOne({email: user.username}, function(err, quser){
                    quser.country = user.country;
                    quser.city = user.city;
                    quser.postcode = user.postcode;
                    quser.instrument = user.instrument;
                    quser.free = user.free;
                    quser.save(function(err){
                        useragent.get('/users/'+quser._id).end(function(err, res){
                            console.log(res.body);
                        });
                        if(user.username === 'mail5@example.biz') {
                            userId = quser._id;
                            done();
                        }
                    });
                });
            });
        });
        it('should add avatar', function(done){
            useragent
                .post('/users/'+userId+'/avatar')
                .attach('media', 'test/samples/fff.jpg')
                .end(function(err, res){
                    if(err){ console.log(err);}
                    res.body.message.should.equal('avatar added');
                    mediaId = res.body.id;
                    setTimeout(function(){
                        done()
                    }, 1900);
            });
        });
    });
    
    describe('add band', function(){
        it('should add band', function(done){
            useragent
                .post('/bands')
                .send({ name: 'Another Band',
                        country: 'Germany',
                        city: 'Cologne',
                        postcode: '51063',
                        genre: ['Heavy Metal', 'Hard Rock'],
                        members: {role: 'guitarist'}
                    })
                .expect(200)
                .end(function(err, res){
                    console.log(res.body);
                    res.body.message.should.equal('Band created');
                    bandId = res.body.id;
                    done();
            });
        });
        
        it('should add members', function(done){
            usamples.users.forEach(function(user){
                if(user.username !== 'mail@example.com'){
                    User.findOne({ email: user.username}, function(err, quser){
                        useragent
                            .post('/bands/'+bandId+'/members')
                            .send({
                                    members: {
                                        user: quser._id,
                                        role: quser.instrument[0]
                                    }
                            })
                            .end(function(err, res){
                                if(user.username === 'mail5@example.biz') {
                                    useragent.get('/bands/'+bandId).end(function(err, res){
                                        console.log(res.body.members);
                                        done();
                                    });
                                }
                            });
                    });
                }
            });
        });
    });
    
    describe('Event', function(){
        it('should be added', function(done){
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
                        gigId = res.body.gigId;
                        done();
                }); 
            });
        
        });
    });
    
    describe('searching musicians', function(){
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
                    //res.body.message.should.equal('No Results');
                    console.log(res.body);
                    done();
            });
        });
    });
    
    describe('casting-session', function(){
        it('should invite user from search result', function(done){
            done();
        });
        
        it('should create session', function(done){
            useragent
                .post('/casting')
                .send({ gig: gigId,
                        musician: userId
                      })
                .expect(200)
                .end(function(err, res){
                    if(err){ console.log(err);}
                    res.body.message.should.equal('Casting-Session created');
                    csId = res.body.id;
                    done();
            });
        });
        
        it('should receive json about session', function(done){
            useragent
                .get('/casting/'+csId)
                .expect(200)
                .end(function(err, res){
                    if(err){ console.log(err);}
                    console.log(res.body);
                    done();
            });
        });
        
        it('should post a comment', function(done){
            useragent
                .post('/casting/'+csId+'/comments')
                .send({ comment: 'Hello World!'})
                .end(function(err, res){
                    if(err){ console.log(err);}
                    res.body.message.should.equal('Comment added');
                    useragent
                        .get('/casting/'+csId)
                        .expect(200)
                        .end(function(err, res){
                            if(err){ console.log(err);}
                            console.log(res.body.comments);
                            commentId = res.body.comments[0]._id;
                            done();
                    });
            })
        });
        
        it('should post another comment', function(done){
            useragent
                .post('/casting/'+csId+'/comments')
                .send({ comment: 'Hello World!!!'})
                .end(function(err, res){
                    if(err){ console.log(err);}
                    res.body.message.should.equal('Comment added');
                    useragent
                        .get('/casting/'+csId)
                        .expect(200)
                        .end(function(err, res){
                            if(err){ console.log(err);}
                            console.log(res.body.comments);
                            //commentId = res.body.comments[1]._id;
                            done();
                    });
            })
        });
        
        it('should get comment', function(done){
            useragent
                .get('/casting/'+csId+'/comments/'+commentId)
                .end(function(err, res){
                    if(err){ console.log(err);}
                    console.log(res.body);
                    done();
            })
        });
        
        it('should edit comment', function(done){
            useragent
                .put('/casting/'+csId+'/comments/'+commentId)
                .send({comment: 'Hallo Welt!!!!'})
                .end(function(err, res){
                    if(err){ console.log(err);}
                    res.body.message.should.equal('Comment updated');
                    done();
            })
        });
        
        it('should delete comment', function(done){
            useragent
                .delete('/casting/'+csId+'/comments/'+commentId)
                .end(function(err, res){
                    if(err){ console.log(err);}
                    res.body.message.should.equal('Comment removed');
                    done();
            })
        });
        
        it('should post media files', function(done){
            useragent
                .post('/casting/'+csId+'/media')
                .attach('media', 'test/samples/fff.jpg')
                .end(function(err, res){
                    if(err){ console.log(err);}
                    res.body.message.should.equal('Media added');
                    mediaId = res.body.id;
                    setTimeout(function(){
                        done()
                    }, 1900);
            });
        });
        
        it('should get media file', function(done){
            useragent
                .get('/casting/'+csId+'/media/'+mediaId)
                .expect(200, done);
        });
    });
    
    after(function(done){
        /*User.remove({}, function(err){
            if(err)
                console.log(err);
            else {
                console.log('users removed');
            }
        });*/
        Band.remove({}, function(err){
            if(err)
                console.log(err);
            else {
                console.log('band removed');
            }
        });
        Event.remove({}, function(err){
            if(err)
                console.log(err);
            else {
                console.log('event removed');
            }
        });
        Gig.remove({}, function(err){
            if(err)
                console.log(err);
            else {
                console.log('gig removed');
            }
        });
        Search.remove({}, function(err){
            if(err)
                console.log(err);
            else {
                console.log('search removed');
            }
        });
        Casting.remove({}, function(err){
            if(err)
                console.log(err);
            else {
                console.log('casting removed');
                
                done();
            }
        });
    });
});