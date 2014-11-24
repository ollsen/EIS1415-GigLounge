var express = require('express');
var router = express.Router();
var title = 'GigLounge - Proof of Concepts';
var db = require('../db');
var User = db.model('User');

var Band = db.model('Band');



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
        return Band.findOne().sort({id: -1}).limit(1).exec(function(err, band) {
            var newBand = new Band();
            if(band)
                newBand.id = parseInt(band.id)+1;
            else
                newBand.id = 1;
            newBand.bandName = req.body.bandName;
            newBand.country = req.body.country;
            newBand.city = req.body.city;
            newBand.genre = req.body.genre;
            newBand.members.push(req.user)
            return newBand.save(function (err) {
                if (!err) {
                    console.log("created with id: "+newBand.id);
                } else {
                    console.log(err);
                }
                Band.findOne({ id: newBand.id}).exec(function(err, band) {
                    req.user.bands.push(band);
                    req.user.save(function(err) {
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
    
    /* GET user Profile */
    router.get('/:id', isAuthenticated, function(req, res) {
        Band.findOne({ id : req.params.id }).populate('members').exec(function(err, db_band){
            if(err) {}
            res.render('bands/profile', {
                title: title,
                user: req.user,
                band: db_band,
                message: req.flash('message')
            });
        });
        
    });
    
    
    return router;
}