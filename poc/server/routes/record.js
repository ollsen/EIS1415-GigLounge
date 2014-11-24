var express = require('express');
var router = express.Router();
var title = 'GigLounge - Proof of Concepts';
var db = require('../db');
var User = db.model('User');
var Audio = db.model('Audio');



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
        res.render('record/record', {
            title: title,
            user: req.user,
            message: req.flash('message')
        });
    });
    
    router.post('/add', isAuthenticated, function(req, res) {
        var audio = new Audio();
        audio._creator = req.user._id;
        audio.file = req.body.audiofile;
        return audio.save(function (err) {
            if (!err) {
                console.log("created with id: "+audio._id);
            } else {
                console.log(err);
            }
            req.user.auTracks.push(audio);
            req.user.save(function(err) {
                if (!err) {
                    console.log("push succeeded");
                } else {
                    console.log(err);
                }
            });
            return res.redirect('/home');
        });
    });

    return router;
}
