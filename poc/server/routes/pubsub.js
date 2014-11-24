var express = require('express');
var router = express.Router();
var methodOverride = require('method-override');
var title = 'GigLounge - Proof of Concepts';
var User = require('../models/user')
var faye = require('faye');

var client = new faye.Client('http://localhost:3000/faye', { timeout: 20 });



var isAuthenticated = function(req, res, next) {
    //if user is authenticated in the session, call the next() to call the next request handler
    //Passport adds this method to request object. A middleware is allowed to add properties to
    // request and response object
    if (req.isAuthenticated())
        return next();
    //if the user is not authenticated then redirect him to the login page
    res.redirect('../');
}

module.exports = function(passport) {
    
    // GET Message Site
    router.get('/message', function(req, res) {
        res.render('pubsub/message', {
            title: title,
            user: req.user,
            message: req.flash('message')
        });
    });
    
    
    
    /* Handle POST Message */
    
    router.post('/message', function(req, res) {
        client.publish('/channel', { text: req.body.message});
        res.send(200);
    });
    
    return router;
}