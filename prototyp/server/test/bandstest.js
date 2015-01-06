var assert = require('assert');
var should = require('should');
var request = require('supertest');
var app = require('../app');
var db = require('../db');
var Admin = db.model('Admin');
//var User = db.model('User');
var useragent = request.agent(app);

var usersjson = require('./samples/users.json');


describe('/bands without login', function() {
    it('should respond with message "Please login"', function(done) {
        useragent
            .get('/bands')
            .expect(200)
            .end(function(err, res){
                res.body.message.should.equal('Please login');
                done();
        });
    });
});

describe('/bands with login', function() {
    var userId;
    var sessionId;
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
                                        done();
                                });
                                //done();
                        });
                    }
                });
        });
    });
    
    it('should receive json message "No Bands"', function(done) {
        useragent
            .get('/bands')
            .expect(200)
            .end(function(err, res){
                res.body.message.should.equal('No Bands');
                done();
        });
    });
    
    describe('Add and manipulate Band Object', function() {
        var bandId;
        it('should add a band', function(done){
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
                    done();
            });
        });
        
        it('should list recently added band', function(done){
            useragent
                .get('/bands')
                .expect(200)
                .end(function(err, res){
                    console.log(res.body);
                    res.body.bands.length.should.equal(1);
                    res.body.bands[0].name.should.equal('Another Band');
                    bandId = res.body.bands[0]._id;
                    done();
            });
        });
        
        it('should get recently added band', function(done){
            useragent
                .get('/bands/'+bandId)
                .expect(200)
                .end(function(err, res){
                    res.body.name.should.equal('Another Band');
                    done();
            });
        });
        
        it('should get recently added band with memberdata', function(done){
            useragent
                .get('/bands/'+bandId)
                .expect(200)
                .end(function(err, res){
                    res.body.members[0].user.should.have.property('firstName');
                    res.body.members[0].permission.should.equal(3);
                    done();
            });
        });
        
        it('should list band in user profile', function(done){
            useragent
                .get('/users/'+sessionId)
                .expect(200)
                .end(function(err, res){
                    res.body.bands[0].name.should.equal('Another Band');
                    res.body.bands[0].should.not.have.property('bands');
                    done();
            });
        });
        
        it('should edit band profile', function(done){
            useragent
                .put('/bands/'+bandId)
                .send({ name: 'Another Band',
                        country: 'Germany',
                        city: 'Cologne',
                        postcode: '51063',
                        genre: ['Heavy Metal', 'Hard Rock']
                    })
                .end(function(err, res){
                    res.body.message.should.equal('Band updated');
                    done();
            });
        });
        
        it('should add a new member to the band', function(done){
            useragent
                .post('/bands/'+bandId+'/members')
                .send({
                        members: {
                            user: userId,
                            role: 'drummer'
                        }
                      })
                .expect(200)
                .end(function(err, res){
                    useragent
                        .get('/bands/'+bandId)
                        .expect(200)
                        .end(function(err, res){
                            console.log(res.body.members[1]);
                            res.body.members[1].user.should.have.property('firstName');
                            done();
                    });
                });
        });
        
        it('should edit members role', function(done){
            useragent
                .put('/bands/'+bandId+'/members')
                .send({
                        members: [{
                            user: sessionId,
                            role: 'gitarist'
                        },
                        {
                            user: userId,
                            role: 'bassist'
                        }
                        ]})
                .end(function(err, res){
                    useragent
                        .get('/bands/'+bandId)
                        .end(function(err, res){
                            res.status.should.equal(200);
                            res.body.members[1].role.should.equal('bassist');
                            done();
                    });
            });
        });
        
        it('should remove a member', function(done){
            useragent
                .delete('/bands/'+bandId+'/members/'+userId)
                .end(function(err, res){
                    res.status.should.equal(200);
                    console.log('GET Band');
                    useragent
                        .get('/bands/'+bandId)
                        .end(function(err, res){
                            res.body.members.length.should.equal(1);
                            useragent
                                .get('/users/'+userId)
                                .end(function(err, res){
                                    done();
                            });
                    });
                });
        });
        
        it('should remove band', function(done){
            useragent
                .delete('/bands/'+bandId)
                .end(function(err, res){
                    res.status.should.equal(200);
                    res.body.message.should.equal('Band removed');
                    useragent
                        .get('/bands')
                        .end(function(err, res){
                            res.body.message.should.equal('No Bands');
                            done();
                    });
            });
            
        })
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