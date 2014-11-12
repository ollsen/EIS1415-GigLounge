var express = require('express');
var router = express.Router();
var title = 'GigLounge - Proof of Concepts';
var User = require('../models/user')
var Band = require('../models/band')
var mongoose = require('mongoose');



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
    /* GET bands listing. */
    router.get('/', isAuthenticated, function(req, res) {
        var bandlist = Band.find({},function(err, db_bands){
            if(err) {}
            res.render('bands/index', {
                title: title,
                user: req.user,
                bandlist: db_bands,
                message: req.flash('message')
            });
        });
        
    });
    
    /* GET band creating page. */
    router.get('/new', isAuthenticated, function(req, res) {
        res.render('bands/new', {
            title: title,
            user: req.user,
            message: req.flash('message')
        });   
    });
    
    /* Handle POST Bandcreation */
    router.post('/new', isAuthenticated, function(req, res) {
        User.findOne({ username : req.body.creator}, function(err, creator) {
            return Band.findOne().sort({id: -1}).limit(1).exec(function(err, band) {
                var newBand = new Band();
                if(band)
                    newBand.id = parseInt(band.id)+1;
                else
                    newBand.id = 1;
                console.log("user id: "+req.body.userId);
                newBand.bandName = req.body.bandName;
                newBand.country = req.body.country;
                newBand.city = req.body.city;
                newBand.genre = req.body.genre;
                newBand.members.push(creator)
                return newBand.save(function (err) {
                    if (!err) {
                        console.log("created with id: "+newBand.id);
                    } else {
                        console.log(err);
                    }
                    Band.findOne({ id: newBand.id}).exec(function(err, band) {
                        creator.bands.push(band);
                        creator.save(function(err) {
                            if (!err) {
                                console.log("push succeeded");
                            } else {
                                console.log(err);
                            }
                        });
                    });

                    return res.redirect('./');
                });
            });
        });
    });
    
    
    return router;
}