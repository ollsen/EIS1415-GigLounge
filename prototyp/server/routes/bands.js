var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
var db = require('../db');
var Admin = db.model('Admin');
var User = db.model('User');
var Band = db.model('Band');

var isAuthenticated = require('./auth');

module.exports = function(passport) {
    
    /* GET Bandlist */
    router.get('/', isAuthenticated, function(req, res) {
        Band.find({},'name', function(err, bands){
            if(err) {console.log('error: '+err);}
            if (!bands.length) {
                res.json({ message : 'No Bands'});
            } else {
                res.json({bands : bands});
            }
        });
    });
    
    /* GET Band */
    router.get('/:_id', isAuthenticated, function(req, res) {
      Band.findOne({_id : req.params._id}).populate('members.user','firstName lastName').exec(function(err, band){
          if(err) {console.log(err)}
          res.json(band);
      });
    });
    
    /* POST new Band */
    router.post('/', isAuthenticated, function(req, res) {
        var newBand = Band();
        var docId = mongoose.Types.ObjectId(req.query.docId);
        newBand._id = docId;
        newBand.name = req.body.name;
        newBand.country = req.body.country;
        newBand.city = req.body.city;
        newBand.postcode = req.body.postcode;
        newBand.members.push({  user: req.user,
                                role: req.body.members.role,
                                permission: 3
                             });
        newBand.save(function (err) {
            if (err) {
                console.log(err);
                res.json({ message : err });
            } else {
                req.user.bands.push(newBand);
                req.user.save(function(err) {
                    if (err) {
                        console.log(err);
                        res.json({ message : err });
                    } else {
                        res.json({ message : 'Band created', id: docId});
                    }
                });
            }
        });
    });
    
    /* PUT existing Band */
    router.put('/:_id', isAuthenticated, function(req, res) {
        Band.findOne({_id : req.params._id}).exec(function(err, band){
            if(err) {console.log(err)}
            band.members.forEach(function(member){
                if(member.user.equals(req.user._id) && member.permission == 3){
                    band.name = req.body.name;
                    band.country = req.body.country;
                    band.lastName = req.body.city;
                    band.postcode = req.body.postcode;
                    band.genre = req.body.genre;
                    band.save(function(err) {
                        if(err){ console.log(err);}
                        else {
                            res.json({ message : 'Band updated'});
                        }
                    });
                } else {
                    res.json({ message : 'Permission denied'});
                }
            });
      });
    });
    
    /* POST member on existing Band */
    router.post('/:_id/members', isAuthenticated, function(req, res) {
        Band.findOne({_id : req.params._id}).exec(function(err, band){
            band.members.push({ user: req.body.members.user,
                                role: req.body.members.role
                                });
            band.save(function (err) {
                if (err) {
                    console.log(err);
                    res.json({ message : err });
                } else {
                    User.findOne({_id : req.body.members.user}).select('-password').exec(function(err, user){
                        user.bands.push(band);
                        user.save(function(err) {
                            if (err) {
                                console.log(err);
                                res.json({ message : err });
                            } else {
                                res.json({ message : 'Member added'});
                            }
                        });
                    });
                }
            });
        });
    });
    
    /* PUT member on existing band */
    router.put('/:_id/members', isAuthenticated, function(req, res) {
        Band.findOne({_id : req.params._id}).exec(function(err, band){
            band.members = req.body.members;
            band.save(function(err) {
                if (err) {
                    console.log(err);
                    res.json({ message : err });
                } else {
                    res.json({ message : 'Members edited'});
                }
            });
        });
    });
    
    /* DELETE Member on existing band */
    router.delete('/:_id/members/:_uid', isAuthenticated, function(req, res) {
        Band.findOneAndUpdate({_id : req.params._id}, {$pull: {members: {user: req.params._uid}}}).exec(function(err, band){
            User.findOne({_id : req.params._uid}).exec(function(err, user){
                user.bands.pull(req.params._id);
                user.save(function(err) {
                    if (err) {
                        console.log(err);
                        res.json({ message : err });
                    } else {
                        res.json({ message : 'Member removed'});
                    }
                });
            });
        });
    });
    
    /* DELETE band */
    router.delete('/:_id', isAuthenticated, function(req, res){
        Band.findOne({_id : req.params._id}).exec(function(err, band){
            band.members.forEach(function(member){
                User.findOne({_id : member.user}).exec(function(err, user){
                user.bands.pull(req.params._id);
                user.save(function(err) {
                    if (err) {
                        console.log(err);
                        res.json({ message : err });
                    } 
                });
            });
            });
            band.remove(function(err){
                res.json({ message : 'Band removed'});
            });
        });
    });
    
    return router;
}