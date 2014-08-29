/** @jsx React.DOM */
var Cockpit = React.createClass({
  getInitialState:function(){
      return {
        //login : 0 do not know, 1 not logged, 2 logged
        login:0
      };
  },
  render:function(){
    return <div>
    <section className="section green">
      <div class="container">
	       <p>IF <input autofocus type="text" placeholder="Yo username" />
	     Yoes FIP <br/>
          then save the current track <br/>to this playlist&nbsp; <span className="playlist">Toto</span>
         </p>
	    </div>
    </section>
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
    </div>
  },
  sendYoers: function(){
  }
});
