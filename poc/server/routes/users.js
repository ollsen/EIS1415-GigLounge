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
            res.render('users/users', {
            title: title,
            user: req.user,
            userlist: db_users,
            message: req.flash('message')
        });
        console.log(db_users.count)
        });
        
    });
    
    /* GET user Profile */
    router.get('/:username', isAuthenticated, function(req, res) {
        User.findOne({ username : req.params.username }).populate('bands').exec(function(err, db_user){
            if(err) {}
            res.render('users/profile', {
                title: title,
                user: req.user,
                userprofile: db_user,
                bands: db_user.populate('bands'),
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
    router.post('/:username/edit', isAuthenticated, function(req, res) {
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

    return router;
}
