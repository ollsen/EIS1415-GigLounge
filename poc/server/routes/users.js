var express = require('express');
var router = express.Router();
var title = 'GigLounge - Proof of Concepts';
var User = require('../models/user')



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
    /* GET users listing. */
    router.get('/', isAuthenticated, function(req, res) {
        var userlist = User.find({},function(err, db_users){
            if(err) {}
            res.render('users', {
            title: title,
            user: req.user,
            userlist: db_users,
            message: req.flash('message')
        });
        });
        
    });
    
    /* GET user Profile */
    router.get('/:username', isAuthenticated, function(req, res) {
        User.findOne({ username : req.params.username },function(err, db_user){
            if(err) {}
            res.render('profile', {
            title: title,
            user: req.user,
            userprofile: db_user,
            message: req.flash('message')
        });
        });
        
    });
    
    /* HANDLE PUT Profile */
    router.put('/:username/edit', isAuthenticated, function(req, res) {
        
    });

    return router;
}
