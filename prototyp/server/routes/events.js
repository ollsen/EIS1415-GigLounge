var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
var db = require('../db');
var User = db.model('User');
var Band = db.model('Band');
var Event = db.model('Event');
var Gig = db.model('Gig');

var isAuthenticated = require('./auth');

module.exports = function(passport) {
    
    /* GET Eventlist */
    router.get('/', isAuthenticated, function(req, res) {
        console.log(req.user);
        Event.find({},'name', function(err, events){
            if(err) {console.log('error: '+err);}
            if (!events.length) {
                res.json({ message : 'No Events'});
            } else {
                res.json({event : events});
            }
        });
    });
    
    /* GET Event */
    router.get('/:_id', isAuthenticated, function(req, res) {
        Event.findOne({_id : req.params._id}).populate('bands','name').exec(function(err, event){
            if(err) {console.log(err)}
            res.json(event);
        });
    });
    
    /* POST add new Event */
    router.post('/', isAuthenticated, function(req, res) {
        var newEvent = Event();
        var docId = mongoose.Types.ObjectId(req.query.docId);
        newEvent._id = docId;
        newEvent.creator = req.user;
        newEvent.name = req.body.name;
        newEvent.location = req.body.location;
        newEvent.country = req.body.country;
        newEvent.city = req.body.city;
        newEvent.postcode = req.body.postcode;
        newEvent.adress = req.body.address;
        newEvent.save(function (err) {
            if (err) {
                console.log(err);
                res.json({ message : err });
            } else {
                req.user.save(function(err) {
                    if (err) {
                        console.log(err);
                        res.json({ message : err });
                    } else {
                        res.json({ message : 'Event created', id : docId});
                    }
                });
            }
        });
    });
    
    /* PUT event */
    router.put('/:_id', isAuthenticated, function(req, res){
        Event.findOne({_id: req.params._id}).exec(function(err, event){
            if(event.creator === req.user) {
                event.name = req.body.name;
                event.location = req.body.location;
                event.country = req.body.country;
                event.city = req.body.city;
                event.postcode = req.body.postcode;
                event.adress = req.body.address;
                event.save(function (err) {
                    if (err) {
                        console.log(err);
                        res.json({ message : err });
                    } else {
                        req.user.save(function(err) {
                            if (err) {
                                console.log(err);
                                res.json({ message : err });
                            } else {
                                res.json({ message : 'Event updated'});
                            }
                        });
                    }
                });
            } else {
                res.json({ message : 'No Permission'});
            }
        })
    });
    
    /* POST new Gig to Event-lineup */
    router.post('/:_id/lineup', isAuthenticated, function(req, res) {
        var gig = new Gig();
        gig.band = req.body.band;
        gig.event = req.params._id;
        console.log(req.body.lineup);
        req.body.lineup.forEach(function(participant){
            gig.lineup.push(participant);
        });
        gig.save(function(err){
            if(err) {console.log(err);}
            Event.findOne({_id : req.params._id}).exec(function(err, event){
                if(err) {console.log(err);}
                event.bands.push(gig);
                event.save(function (err) {
                    if (err) {
                        console.log(err);
                        res.json({ message : err });
                    } else {
                        req.user.save(function(err) {
                            if (err) {
                                console.log(err);
                                res.json({ message : err });
                            } else {
                                res.json({ message : 'Band added', gigId: gig._id});
                            }
                        });
                    }
                });
            });
        });
    });
    
    /*
    * DELETE Event
    */
    
    router.delete('/:_id', isAuthenticated, function(req, res){
        Event.findById(req.params._id, function(err, event){
            event.remove(function(err){
                res.json({ message : 'Event removed'});
            });
        });
    });
    
    return router;
}