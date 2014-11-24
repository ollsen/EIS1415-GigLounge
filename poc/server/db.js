
var url = 'mongodb://localhost/poc';
/* Database */
var dbConfig = require('./db');
var mongoose = require('mongoose');
var Grid = require('gridfs-stream');
var conn = mongoose.createConnection(url);
//mongoose.connect(dbConfig.url);
//var db = mongoose.connection;
conn.once('open', function() {
    var gfs = Grid(conn.db, mongoose.mongo);
});

module.exports = conn;
