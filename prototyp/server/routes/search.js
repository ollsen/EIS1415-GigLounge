var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
var request = require('request');
var db = require('../db');
var Admin = db.model('Admin');
var User = db.model('User');
var Search = db.model('Search');

var isAuthenticated = require('./auth');

module.exports = function(passport) {
    
    /* GET Search */
    router.get('/:_id', isAuthenticated, function(req, res) {
        Search.findById(req.params._id, function(err, search){
            if(err) {console.log('error: '+err);}
            request('http://api.geonames.org/findNearbyPostalCodesJSON?postalcode='+search.postcode+'&country='+search.country+'&radius='+search.radius+'&maxRows=500&style=SHORT&username=ollsen', function(err, response) {
                if(response.statusCode == 200) {
                    var postCodes = new Array();
                    var object = JSON.parse(response.body);
                    object.postalCodes.forEach(function(postcode){
                        postCodes.push(postcode.postalCode);
                    });
                    
                    User.find({free: true, instrument: search.instrument})
                    .where('postcode')
                    .in(postCodes)
                    .select('firstName lastName postcode instrument')
                    .exec(function(err, result){
                        if(result.length){
                            res.json({result: result});
                        } else {
                            res.json({message: 'No Results'});
                        }
                    });
                    
                } else {
                    res.json({message: 'error'});
                }
            });
        });
    });
    
    /* POST Search */
    router.post('/', isAuthenticated, function(req, res){
        var newSearch = Search();
        var docId = mongoose.Types.ObjectId(req.query.docId);
        newSearch._id = docId;
        newSearch.instrument = req.body.instrument;
        newSearch.genre = req.body.genre;
        newSearch.country = req.body.country;
        newSearch.city = req.body.city;
        newSearch.postcode = req.body.postcode;
        newSearch.radius = req.body.radius;
        newSearch.save(function(err) {
            if (err) {
                console.log(err);
                res.json({ message : err });
            } else {
                res.json({ message : 'Search added', id: docId});
            }
        });
    });
    
    /* PUT Search */
    router.post('/:_id', isAuthenticated, function(req, res){
        Search.findById(req.params._id, function(err, search) {
            newSearch._id = docId;
            search.instrument = req.body.instrument;
            search.genre = req.body.genre;
            search.country = req.body.country;
            search.city = req.body.city;
            search.postcode = req.body.postcode;
            search.radius = req.body.radius;
            search.save(function(err) {
                if (err) {
                    console.log(err);
                    res.json({ message : err });
                } else {
                    res.json({ message : 'Search updated'});
                }
            });
        });
    });
    
    /* DELETE Search */
    router.delete('/:_id', isAuthenticated, function(req, res){
        User.remove({_id : req.params._id}, function(err){
            if(err) {console.log(err)}
            else {
                res.json({ message : 'search removed'});
            }
        });
    });
    
    
    return router;
}