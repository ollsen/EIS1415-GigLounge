var mongoose = require('mongoose');

var MyModel = new mongoose.Schema({
    gig: {type: mongoose.Schema.Types.ObjectId, ref: 'Gig'},
    instrument: String,
    genre: String,
    country: String,
    city: String,
    postcode: String,
    radius: Number
});

module.exports = mongoose.model('Search', MyModel);