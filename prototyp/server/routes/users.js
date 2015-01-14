var express = require('express');
var router = express.Router();
var db = require('../db');
var User = db.model('User');
var Admin = db.model('Admin');
var busboy = require('connect-busboy');

var isAuthenticated = require('./auth');
function getAdminquery(req) {
    var query = Admin.findOne({user: req.session.passport.user});
    return query;
}

/*
* Setting up gridFS Stream for media uploads
*/
var Grid = require('gridfs-stream');
var mongoose = require('mongoose');
var gfs;
db.once('open', function(){
    gfs = Grid(db.db);
});


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
    
    /* POST new avater */
    router.post('/:_id/avatar', function(req, res){
        getAdminquery(req).exec(function(err, perm){
            if(req.session.passport.user === req.params._id || (perm != null && perm.system)) {
                User.findById(req.params._id, function(err, user){
                    if(err) {console.log(err)}
                    var mediaId = mongoose.Types.ObjectId(req.query.docId);
                    var stream;
                    req.pipe(req.busboy);
                    req.busboy.on('file', function(fieldname, file, filename){
                        console.log("Uploading: "+filename);

                        stream = gfs.createWriteStream({
                            _id : mediaId,
                            filename : filename,
                            mode: 'w',
                            content_type: 'image/jpg'
                        });
                        file.pipe(stream);
                    });
                    user.avatar = mediaId;
                    user.save(function (err){
                        if (err){ console.log(err);}
                        else { res.json({ message : 'avatar added' });}
                    });
                });
            } else {
                res.json({ message : 'No Permission'});
            }
        });
    });
    
    /* GET avatar */
    router.get('/:_id/avatar', function(req, res){
        User.findById(req.params._id, function(err, user){
            var readstream = gfs.createReadStream({
                _id: user.avatar
            });
            //var filename = 'avatar.jpg'
            //res.setHeader('Content-disposition','attachment; filename='+ filename);
            res.setHeader('Content-type', 'image/jpg');
            readstream.pipe(res);
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
