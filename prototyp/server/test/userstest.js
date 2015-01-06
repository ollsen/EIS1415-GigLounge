var assert = require('assert');
var should = require('should');
var request = require('supertest');
var app = require('../app');
var db = require('../db');
var Admin = db.model('Admin');
var useragent = request.agent(app);

var usersjson = require('./samples/users.json');


describe('/users without login', function() {
    it('should respond with message "Please login"', function(done) {
        useragent
            .get('/users')
            .expect(200)
            .end(function(err, res){
                res.body.message.should.equal('Please login');
                done();
        });
    });
});

describe('/users with login', function() {
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
                                console.log(res.body.message);
                                done();
                        });
                    }
                });
        });
    });
    
    it('should receive json with expected properties', function(done){
        useragent
            .get('/users')
            .expect(200)
            .end(function(err, res){
                res.body.should.have.property('users').with.lengthOf(5);
                res.body.users[0].should.have.property('_id');
                res.body.users[0].should.have.property('firstName');
                res.body.users[0].should.have.property('lastName');
                res.body.users[0].should.not.have.property('email');
                res.body.users[0].should.not.have.property('password');
                sessionId = res.body.users[0]._id;
                userId = res.body.users[1]._id;
                done();
        });
    });
    
    it('should GET /users/:_id and receive json of user profile', function(done){
        useragent
            .get('/users/'+userId)
            .expect(200)
            .end(function(err, res) {
                res.body._id.should.equal(userId);
                res.body.should.have.property('_id');
                res.body.should.have.property('email');
                res.body.should.have.property('firstName');
                res.body.should.have.property('lastName');
                res.body.should.not.have.property('password');
                done();
            });
    });
    
    it('should add Data to user profile with PUT', function(done){
        useragent
            .put('/users/'+sessionId)
            .send({country : 'Germany'})
            .expect(200)
            .end(function(err, res){
                res.body.message.should.equal('updated');
                done();
        })
    });
    
    it('shouldnt add Data to other user profile with PUT', function(done){
        useragent
            .put('/users/'+userId)
            .send({country : 'Germany'})
            .expect(200)
            .end(function(err, res){
                res.body.message.should.equal('No Permission');
                done();
        })
    });
    
    describe('/PUT admin permission', function() {
        before(function(done){
            var newAdmin = new Admin();
            newAdmin.user = sessionId;
            newAdmin.system = true;
            newAdmin.save(function(err) {
                if (err) {console.log(err);}
                else {
                    console.log('perm added');
                    done();
                }
            });
        });
        
        it('should add Data from other user with admin permission', function(done){
        
            useragent
                .put('/users/'+userId)
                .send({country : 'Germany'})
                .expect(200)
                .end(function(err, res){
                    res.body.message.should.equal('updated');
                    done();
            });
        });
        
        after(function(done){
            Admin.remove({user : sessionId}, function(err){
                if(err) console.log(err);
            });
            done();
        });
    }); 
    
    
    
    it('should GET /users/:_id and receive updated json of user profile', function(done){
        useragent
            .get('/users/'+sessionId)
            .expect(200)
            .end(function(err, res) {
                console.log(res.body);
                res.body._id.should.equal(sessionId);
                res.body.country.should.equal('Germany');
                done();
            });
    });
    
    it('should GET /users/:_id and receive not-updated json of other user profile', function(done){
        useragent
            .get('/users/'+userId)
            .expect(200)
            .end(function(err, res) {
                console.log(res.body);
                res.body._id.should.equal(userId);
                res.body.should.not.have.property('country');
                done();
            });
    });
    
    describe('/DELETE admin permission', function() {
        before(function(done){
            var newAdmin = new Admin();
            newAdmin.user = sessionId;
            newAdmin.system = true;
            newAdmin.save(function(err) {
                if (err) {console.log(err);}
                else {
                    console.log('perm added');
                    done();
                }
            });
        });
        
        it('should remove user from admin', function(done) {
            useragent
                .delete('/users/'+userId)
                .expect(200)
                .end(function(err, res){
                    res.body.message.should.equal('user removed');
                    done();
            });
        });
        
        after(function(done){
            Admin.remove({user : sessionId}, function(err){
                if(err) console.log(err);
            });
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
            done();
        });
});