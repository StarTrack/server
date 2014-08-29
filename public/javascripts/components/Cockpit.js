/** @jsx React.DOM */
var Cockpit = React.createClass({
  getInitialState:function(){
      return {
        user:null
      };
  },
  render:function(){
    var debouncedSend = _.debounce( this.sendYoers, 200 );
    var options = _.map(this.props.user.playlists, function(p){
      return <option id={p.id} data-playlistid={p.id}>{p.name}</option>;
    });
    return <div>
      <section className="section green">
        <div class="container">
          <p>IF
            <input autofocus className="yoUser" type="text" placeholder="Yo username" onKeyUp={debouncedSend}/>
            Yoes FIP <br/>
            then save the current track <br/>to this playlist&nbsp;
            <select className="playlist" onChange={this.sendYoers}>
            {options}
            </select>
          </p>
        </div>
      </section>
    </div>

    /**
     
    <section className="section history">
      <div className="container">
        <h2>STARRED TRACKS HISTORY</h2>
        <table>
          <tr>
            <th>WHEN</th>
            <th>WHO</th>
            <th>WHAT</th>
            <th>STATUS</th>
          </tr>
          <tr>
            <td>2014-29-08 01:32PM</td>
            <td>MIOSSEC</td>
            <td>BOIRE</td>
            <td>OK</td>
          </tr>
          <tr>
            <td>2014-29-08 01:32PM</td>
            <td>MIOSSEC</td>
            <td>BAISER</td>
            <td>FAILED</td>
          </tr>
          <tr>
            <td>2014-29-08 01:32PM</td>
            <td>MIOSSEC</td>
            <td>BAISER</td>
            <td>FAILED</td>
          </tr>
          <tr>
            <td>2014-29-08 01:32PM</td>
            <td>MIOSSEC</td>
            <td>BAISER</td>
            <td>FAILED</td>
          </tr>
        </table>
      </div>
    </section>
     * */
  },
  componentWillReceiveProps: function( props ){
    this.setState({
      user: props.user
    });
  },
  sendYoers: function(){
    var yoUser = this.getDOMNode().querySelector('.yoUser').value;
    var currentPlaylist = this.getDOMNode().querySelector('.playlist').selectedOptions.item().dataset.playlistid;
    var data = JSON.stringify({
      "yoAccounts":[yoUser] ,
      "playlistId": currentPlaylist
    });
    jQuery.ajax({
      method: 'PUT',
      url: '/users/'+this.props.user.login,
      data: data,
      contentType:'application/json'
    }).then( function(){
      console.log("just add a yo listener");
    }, function(){
      console.log("FAIL");
    });
  }
});
