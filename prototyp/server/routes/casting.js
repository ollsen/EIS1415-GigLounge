var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
var busboy = require('connect-busboy');
var Grid = require('gridfs-stream');
var db = require('../db');
var User = db.model('User');
var Band = db.model('Band');
var Gig = db.model('Gig');
var Casting = db.model('Casting');

/*
* Setting up gridFS Stream for media uploads
*/
var gfs;
db.once('open', function(){
    gfs = Grid(db.db);
});

var isAuthenticated = require('./auth');

module.exports = function(passport) {
    
    /*
    * POST new session
    */
    
    router.post('/', isAuthenticated, function(req, res){
        var docId = mongoose.Types.ObjectId(req.query.docId);
        var newCasting = Casting();
        newCasting._id = docId;
        newCasting.gig = req.body.gig;
        newCasting.musician = req.body.musician;
        newCasting.save(function(err){
            if(err) {console.log(err);}
            res.json({ message: 'Casting-Session created', id: docId});
        });
    });
    
    /*
    * GET existing session
    */
    
    router.get('/:_id', isAuthenticated, function(req, res){
        Casting.findById(req.params._id)
        .populate('gig', 'event lineup')
        .populate('musician', 'firstName lastName')
        .populate('comments.author', 'firstName lastName')
        .populate('media.author', 'firstName lastName')
        .exec(function(err, casting){
            if(err){console.log(err);}
            Gig.populate(casting, [{
                path: 'gig.event',
                select: 'name',
                model: 'Event'
            },{
                path: 'gig.lineup.user',
                select: 'firstName lastName',
                model: 'User'
            }],function(err){
                res.json(casting);
            });
        });
    });
    
    /* 
    * GET band's session overview
    */
    
    router.get('/', isAuthenticated, function(req, res){
        
    });
    
    /*
    * MEDIA
    */
    // GET medialist
    router.get('/:_id/media', isAuthenticated, function(req, res){
        
    });
    
    // POST new media
    router.post('/:_id/media', isAuthenticated, function(req, res){
        Casting.findById(req.params._id, function(err, casting){
            var docId = mongoose.Types.ObjectId(req.query.docId);
            var mediaId = mongoose.Types.ObjectId(req.query.docId);
            var stream;
            req.pipe(req.busboy);
            req.busboy.on('file', function(fieldname, file, filename){
                console.log("Uploading: "+filename);
                
                stream = gfs.createWriteStream({
                    _id : mediaId,
                    filename : filename,
                    mode: 'w'
                });
                file.pipe(stream);
            });
            casting.media.push({
                _id: docId,
                author: req.user._id,
                media: mediaId
            });
            casting.save(function(err){
                if(err){console.log(err);}
                else
                    res.json({message: 'Media added', id: docId});
            });
        });
    });
    
    // Get Specific media
    router.get('/:_id/media/:_mid', function(req, res){
        Casting.findById(req.params._id)
        .select('media').populate('media.author', 'firstName lastName').exec(function(err, casting){
            casting.media.forEach(function(media){
                if(media._id == req.params._mid) {
                    //comment.comment = req.body.comment;
                    var readstream = gfs.createReadStream({
                        _id: media.media
                    });
                    return readstream.pipe(res);
                }
            });
        });
    });
    
    /*
    * COMMENTS
    */
    
    // GET Commentlist
    router.get('/:_id/comments', isAuthenticated, function(req, res){
        
    });
    
    // POST new comment
    router.post('/:_id/comments', isAuthenticated, function(req, res){
        Casting.findById(req.params._id, function(err, casting){
            var docId = mongoose.Types.ObjectId(req.query.docId);
            casting.comments.push({_id: docId, author: req.user._id, comment: req.body.comment});
            console.log('second');
            casting.save(function(err){
                if(err){console.log(err);}
                else {res.json({message: 'Comment added', id: docId});}
            });
        });
    });
    
    // GET specific comment
    router.get('/:_id/comments/:_cid', isAuthenticated, function(req, res){
        Casting.findById(req.params._id)
        .select('comments').populate('comments.author', 'firstName lastName').exec(function(err, casting){
            casting.comments.forEach(function(comment){
                if(comment._id == req.params._cid)
                    return res.json(comment);
            });
        });
    });
    
    // PUT edit a specific comment
    router.put('/:_id/comments/:_cid', isAuthenticated, function(req, res){
        Casting.findById(req.params._id)
        .select('comments').populate('comments.author', 'firstName lastName').exec(function(err, casting){
            casting.comments.forEach(function(comment){
                if(comment._id == req.params._cid) {
                    comment.comment = req.body.comment;
                    return;
                }
            });
            casting.save(function(err){
                if(err){console.log(err);}
                res.json({message: 'Comment updated'});
            });
        });
    });
    
    // DELETE specific comment
    router.delete('/:_id/comments/:_cid', isAuthenticated, function(req, res){
        Casting.findById(req.params._id)
        .select('comments').populate('comments.author', 'firstName lastName').exec(function(err, casting){
            casting.comments.pull({_id: req.params._cid});
            casting.save(function(err){
                if(err){console.log(err);}
                console.log(casting.comments);
                res.json({message: 'Comment removed'});
            });
        });
    });
    
    /*
    * DELETE Session
    */
    
    router.delete('/:_id', isAuthenticated, function(req, res){
        Casting.findById(req.params._id, function(err, casting){
            casting.remove(function(err){
                res.json({ message : 'Casting removed'});
            });
        });
    });
    
    return router;
}