var express = require('express');
var router = express.Router();
var title = 'GigLounge - Proof of Concepts';

var isAuthenticated = function(req, res, next) {
    //if user is authenticated in the session, call the next() to call the next request handler
    //Passport adds this method to request object. A middleware is allowed to add properties to
    // request and response object
    if (req.isAuthenticated())
        return next();
    //if the user is not authenticated then redirect him to the login page
    res.redirect('/');
}


module.exports = function(passport) {
    /* GET login page */
    router.get('/', function(req, res){
        if(req.isAuthenticated())
            res.redirect('/home');
        else
            //Display the login page with any flash message, if any
            res.render('index', {
                title: title,
                message: req.flash('message') 
            });
    });
    
    /* Handle login POST */
    router.post('/login', passport.authenticate('login', {
        successRedirect: '/home',
        failureRedirect: '/',
        failureFlash: true
    }));
    
    /* GET Registration Page */
    router.get('/signup', function(req, res){
        res.render('register', {message: req.flash('message')});
    });
    
    /* Handle Registration POST */
    router.post('/signup', passport.authenticate('signup', {
        successRedirect: '/home',
        failureRedirect: '/signup',
        failureFlash: true
    }));
    
    /* GET Home Page */
    router.get('/home', isAuthenticated, function(req,res) {
        res.render('home', { 
            title: title,
            user: req.user 
        });
    });
    
    /* Handle Logout */
    router.get('/signout', function(req, res) {
        req.logout();
        res.redirect('/');
    });
    
    return router;
}
