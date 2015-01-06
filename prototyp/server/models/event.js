var mongoose = require('mongoose');

var MyModel = new mongoose.Schema({
    creator: { type: mongoose.Schema.Types.ObjectId, ref: 'User'},
    name: {type: String, required: true},
    location: String,
    country: String,
    city: String,
    postcode: String,
    address: String,
    bands: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Gig'}],
    start: Date,
    end: Date
});

module.exports = mongoose.model('Event', MyModel);