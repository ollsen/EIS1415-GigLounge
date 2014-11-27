var express = require('express');
var router = express.Router();
var methodOverride = require('method-override');
var title = 'GigLounge - Proof of Concepts';
var db = require('../db');
//var User = require('../models/user')
var User = db.model('User');



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
            res.render('users/index', {
            title: title,
            user: req.user,
            userlist: db_users,
            message: req.flash('message')
        });
        console.log(db_users.count)
        });
        
    });
    
    router.get('/mlist', isAuthenticated, function(req, res) {
        var userlist = User.find({},'-_id username',function(err, db_users){
            if(err) {}
            res.json({users : db_users});
        });
    });
    
    router.get('/mlist/:username', isAuthenticated, function(req, res) {
        User.findOne({ username : req.params.username }).populate('bands').populate('auTracks').exec(function(err, db_user){
            if(err) {}
            res.json(db_user);
        });
    });
    
    /* GET user Profile */
    router.get('/:username', isAuthenticated, function(req, res) {
        User.findOne({ username : req.params.username }).populate('bands').populate('auTracks').exec(function(err, db_user){
            if(err) {}
            res.render('users/profile', {
                title: title,
                user: req.user,
                userprofile: db_user,
                message: req.flash('message')
            });
        });
        
    });
    
    /* GET useredit Page */
    router.get('/:username/edit', isAuthenticated, function(req, res) {
        res.render('users/editprofile', {
            title: title,
            user: req.user,
            message: req.flash('message')
        }); 
    });
    
    /* HANDLE PUT Profile */
    router.put('/:username/edit', isAuthenticated, function(req, res) {
        return User.findOne({username : req.params.username}, function(err, db_user) {
            db_user.email = req.body.email;
            db_user.firstName = req.body.firstName;
            db_user.lastName = req.body.lastName;
            db_user.country = req.body.country;
            db_user.city = req.body.city;
            db_user.mInstrument = req.body.mInstrument;
            return db_user.save(function (err) {
                if (!err) {
                    console.log("updated");
                } else {
                    console.log(err);
                }
                
                return res.redirect('./');
            });
        });
    });

    /* Handle DELETE Profile */
    router.delete('/:username/delete', isAuthenticated, function(req, res) {
        console.log(req.params.id);
        return User.remove({_id: req.user._id}, function(err){
            if(err)
                res.json(err);
            else
                res.redirect('/home');
        });
    });
    return router;
}
