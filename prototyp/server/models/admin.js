var mongoose = require('mongoose');

var MyModel = new mongoose.Schema({
    user : [{ type: mongoose.Schema.Types.ObjectId, ref: 'User'}],
    system: { type: Boolean, default: false},
    moderator: { type: Boolean, default: false}
});

module.exports = mongoose.model('Admin', MyModel);