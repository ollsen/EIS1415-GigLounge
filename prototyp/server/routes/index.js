var express = require('express');
var router = express.Router();
var db = require('../db');
var User = db.model('User');

var isAuthenticated = require('./auth');

module.exports = function(passport) {
    
    // GET home page.
    router.get('/', function(req, res) {
      res.json({ message: 'ok'});
    });

    /*
    * Routes for authentification
    */
    
    // POST Handle Signup
    router.post('/signup', function(req, res, next) {
        passport.authenticate('signup', function(err, user, info) {
            if (err) {return next(err);}
            if (!user) {
                return res.json(info);
            }
            return res.json(info);
        })(req, res, next);
    });
    
    // POST Handle Login
    router.post('/login', function(req, res, next) {
        passport.authenticate('login', function(err, user, info) {
            if (err) {return next(err);}
            if (!user) {
                return res.json(info);
            }
            req.logIn(user, function(err) {
                if (err) { return next(err); }
                return res.json(info);
            });
        })(req, res, next);
    });
    
    /* Handle Logout */
    router.get('/signout', function(req, res) {
        req.logout();
        res.json({message : 'logout Successfull'});
    });
    
    /*
    * GET Home
    */
    router.get('/home', isAuthenticated, function(req, res) {
<<<<<<< HEAD
        res.json({message : 'welcome', id: req.session.passport.user});
=======
        User.findById(req.session.passport.user, function(err, user){
            res.json({message: 'welcome',
                      user: user});
        });
        //res.json({message : 'welcome'});
>>>>>>> origin/prototyp_android
    });
    
   return router;
}
