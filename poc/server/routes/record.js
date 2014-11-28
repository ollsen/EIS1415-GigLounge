var express = require('express');
var router = express.Router();
var title = 'GigLounge - Proof of Concepts';
//var db = require('../db');

var url = 'mongodb://localhost/poc';
var busboy = require('connect-busboy');
/* Database */
var dbConfig = require('../db');
var mongoose = require('mongoose');
var Grid = require('gridfs-stream');
Grid.mongo = mongoose.mongo;
var conn = mongoose.createConnection(url);
var gfs;
//mongoose.connect(dbConfig.url);
//var db = mongoose.connection;
conn.once('open', function() {
    gfs = Grid(conn.db);
});
//db.model()
var User = conn.model('User');
var Audio = conn.model('Audio');




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
        var docId = mongoose.Types.ObjectId(req.query.docId);
        var fstream;
        req.pipe(req.busboy);
        req.busboy.on('file', function(fieldname, file, filename) {
            console.log("Uploading: "+ filename);
            fstream = gfs.createWriteStream({
                _id: docId,
                filename: filename
            });
            file.pipe(fstream);
        });
        var audio = new Audio();
        audio._creator = req.user._id;
        audio.file = docId;
        
        return audio.save(function (err) {
            if (!err) {
                console.log("created with id: "+audio._id+"docId: "+docId);
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
    
    router.get('/play/:_id', isAuthenticated, function(req, res) {
        res.set('Content-Type', 'audio/m4a');
        var readstream = gfs.createReadStream({
            _id: req.params._id
        });
        readstream.pipe(res);
    });

    return router;
}
