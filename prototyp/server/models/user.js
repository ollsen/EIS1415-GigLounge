var mongoose = require('mongoose');

var MyModel = new mongoose.Schema({
    email: {type: String, required: true},
    password: {type: String, required: true},
    firstName: {type: String, required: true},
    lastName: {type: String, required: true},
    birthday: Date,
    country: String,
    city: String,
    postcode: String,
    role: String,
    bands: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Band'}],
    //genre: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Genre'}],
    instrument: [String],
    free: { type: Boolean, default: false},
    gcmRegId: String,
});

module.exports = mongoose.model('User', MyModel);