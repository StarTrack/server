/** @jsx React.DOM */
var Cockpit = React.createClass({
  getInitialState:function(){
      return {
        user:null
      };
  },
  render:function(){
    console.log(this.state);
    var debouncedSend = _.debounce( this.sendYoers, 200 );
    var options = _.map(this.props.user.playlists, function(p){
      return <option id={p.id}>{p.name}</option>;
    });
    return <section className="yoers">
      <div className="yoer10">IF
      <input className="yoUser" type="text" placeholder="Yo username" onKeyUp={debouncedSend}/>
       Yoes FIP
       then save to playlist&nbsp;
       <select className="playlist">
        {options}
       </select>
      </div>
    </section>;
  },
  componentWillReceiveProps: function( props ){
    this.setState({
      user: props.user
    });
  },
  sendYoers: function(){
    var yoUser = this.getDOMNode().querySelector('.yoUser').value;
    var data = JSON.stringify({
      "yoAccounts":[yoUser] ,
      "playlistId": ""
    });
    jQuery.ajax({
      method: 'PUT',
      url: '/users/'+this.state.user.login,
      data: data,
      contentType:'application/json'
    }).then( function(){
      console.log("just add a yo listener");
    }, function(){
      console.log("FAIL");
    });
  }
});
