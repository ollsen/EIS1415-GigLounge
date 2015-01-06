var express = require('express');
var router = express.Router();
var db = require('../db');
var User = db.model('User');
var Admin = db.model('Admin');

var isAuthenticated = require('./auth');
function getAdminquery(req) {
    var query = Admin.findOne({user: req.session.passport.user});
    return query;
}

module.exports = function(passport) {
    /* GET users listing. */
    router.get('/', isAuthenticated, function(req, res) {
      User.find({},'firstName lastName', function(err, users){
          if(err) {console.log(err)}
          res.json({users : users});
      });
    });
    
    /* GET users listing. */
    router.get('/:_id', isAuthenticated, function(req, res) {
      User.findOne({_id : req.params._id}).populate('bands', 'name').select('-password').exec(function(err, user){
          if(err) {console.log(err);}
          res.json(user);
      });
    });
    
    /* PUT users listing. */
    router.put('/:_id', isAuthenticated, function(req, res) {
        getAdminquery(req).exec(function(err, perm){
            if(req.session.passport.user === req.params._id || (perm != null && perm.system)) {
                User.findOne({_id : req.params._id}, function(err, user){
                    if(err) {console.log(err)}
                    if(req.body.email) {
                        user.email = req.body.email;
                    }
                    if(req.body.firstName) {
                        user.firstName = req.body.firstName;
                    }
                    if(req.body.lastName) {
                        user.lastName = req.body.lastName;
                    }
                    if(req.body.birthday) {
                        user.birthday = req.body.birthday;
                    }
                    if(req.body.country) {
                        user.country = req.body.country;
                    }
                    user.save(function (err){
                        if (err){ console.log(err);}
                        else { res.json({ message : 'updated' });}
                    });
                });
            } else {
                res.json({ message : 'No Permission'});
            }
        });
    });
    
    router.delete('/:_id', isAuthenticated, function(req, res){
        getAdminquery(req).exec(function(err, perm){
            if(req.session.passport.user === req.params._id || (perm != null && perm.system)) {
                User.remove({_id : req.params._id}, function(err){
                    if(err) {console.log(err)}
                    else {
                        res.json({ message : 'user removed'});
                    }
                });
            } else {
                res.json({ message : 'No Permission'});
            }
        })
    });
    
    return router;
}
