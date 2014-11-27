
var url = 'mongodb://localhost/poc';
/* Database */
var dbConfig = require('./db');
var mongoose = require('mongoose');
var Grid = require('gridfs-stream');
Grid.mongo = mongoose.mongo;
var conn = mongoose.createConnection(url);
//mongoose.connect(dbConfig.url);
//var db = mongoose.connection;
conn.once('open', function() {
    var gfs = Grid(conn.db);
});

module.exports = conn;
