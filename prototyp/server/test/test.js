var should = require('should');
var request = require('supertest');

var app = require('../app');

var jsonusers = require('./samples/users');
var user1 = jsonusers.users[0];
var useragent = request.agent(app);

describe('GET /', function() {
    it('should respond with plain text', function(done) {
        useragent
            .get('/')
            .expect(200, done);
    });
    
    it('should respond with json', function(done) {
        useragent
            .get('/json')
            .set('Accept', 'application/json')
            .expect('Content-Type', 'application/json')
            .expect(200, done);
    });
});

describe('Passport', function() {
    
    describe('/POST', function() {
        it('should send data', function(done) {
            useragent
                .post('/send')
                .send({ username: 'mail@example.com'})
                .expect('mail@example.com')
                .expect(200, done);
        })
    });
    
    describe('Signup', function() {
        it('should send registration data and receive message: User Registration succesful', function(done) {
            useragent
                .post('/signup')
                .send(user1)
                .expect(200)
                .end(function(err, res){
                   res.body.message.should.equal('User Registration succesful');
                    done();
                });
        });
        
        it('should respond /home with with message "please login" after signup', function(done) {
            useragent
                .get('/home')
                .expect(200)
                .end(function(err, res){
                    res.body.message.should.equal('Please login');
                    done();
            });
        });
        
        it('should send registration data and receive message: User Already exists', function(done) {
           useragent
            .post('/signup')
            .send(user1)
            .expect(200)
            .end(function(err, res){
               res.body.message.should.equal('User Already exists');
                done();
            });
            
       });
        after(function(done){
            
            var db = require('../db');
            var User = db.model('User');
            User.remove({email: user1.username}, function(err){
                if(err)
                    console.log(err);
                else
                    console.log('user removed');
            });
            done();
        });
    });
    
    describe('Login', function(){
        before(function(done){
            useragent
                .post('/signup')
                .send(user1)
                .expect(200, done);
        });
        
        it('should send login data and receive message: Login Succesful', function(done){
            useragent
                .post('/login')
                .send({ username: user1.username,
                        password: user1.password
                      })
                .expect(200)
                .end(function(err, res){
                    res.body.message.should.equal('Login Succesful');
                    done();
            });
        });
        
        it('should respond call /home with message "welcome"', function(done){
            useragent
                .get('/home')
                .expect(200)
                .end(function(err, res) {
                    res.body.message.should.equal('welcome');
                    done();
            });
        })
        
        it('should logout', function(done) {
            useragent
                .get('/signout')
                .expect(200)
                .end(function(err, res) {
                    res.body.message.should.equal('logout Successfull');
                    done();
                });
        });
        
        it('should respond call /home with message "please login" after logout', function(done){
            useragent
                .get('/home')
                .expect(200)
                .end(function(err, res) {
                    res.body.message.should.equal('Please login');
                    done();
            });
        });
        
        it('should send login data and receive message: User not found', function(done){
            useragent
                .post('/login')
                .send({ username: 'max@mustermann.com',
                        password: user1.password
                      })
                .expect(200)
                .end(function(err, res){
                    res.body.message.should.equal('User not found');
                    done();
            });
        });
        
        it('should send login data and receive message: Invalid Password', function(done){
            useragent
                .post('/login')
                .send({ username: user1.username,
                        password: '123456'
                      })
                .expect(200)
                .end(function(err, res){
                    res.body.message.should.equal('Invalid Password');
                    done();
            });
        });
        
        after(function(done){
            
            var db = require('../db');
            var User = db.model('User');
            User.remove({email: user1.username}, function(err){
                if(err)
                    console.log(err);
                else
                    console.log('user removed');
            });
            done();
        });
    });
});