var mongoose = require('mongoose');

var MyModel = new mongoose.Schema({
    name: {type: String, required: true},
    creation_date: {type: Date, default: Date.now()},
    country: String,
    city: String,
    postcode: String,
    genre: [String],
    members: [{
        _id: false,
        user : { type: mongoose.Schema.Types.ObjectId, ref: 'User'},
        role : String,
        permission: { type: Number, default: 1}
    }],
    events: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Event' }],
    avatar: mongoose.Schema.Types.ObjectId
});


module.exports = mongoose.model('Band', MyModel);