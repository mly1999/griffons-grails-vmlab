<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>
	<title> Griffons - Welcome to our Cloud</title>
	<style type="text/css">
	#main{
		text-align: center;
		margin-top: 20px;
		margin-bottom: 20px;
		border-style: groove;
	}
	#footer
	{
		text-align: center;
		margin-top: 20px;
		margin-bottom: 20px;
		border-style: groove;
	}
	</style>
	
	<script type="text/javascript">
		function displayChange(){
			var dropDown = document.getElementById("dep");
			var field = document.getElementById("vm");
			field.value = dropDown.options[dropDown.selectedIndex].value;
		}
	</script>
</head>

<body>
	<div class="title"><h1 align="center" ><font size="18"> Cloud Access </font></h1></div>

<div id="main">
	<form name="VManagerUI" method="post" action="">

<label >Select Template</label>
<select name="temp" id="template">
	<option  value="griffons_ubuntu_12.04_desktop">Ubuntu 12.04</option>
	<option  value="griffons_linuxmint_14.1_cinnamon_desktop">Mint 14.1</option>
	<option  value="griffons_peppermint_3_20120722_desktop">PepperMint 3</option>
</select>
 
<br/><br/>

<label>CPU resource</label>
<select name="cpu" >
	<option value="1">1 CPU - 2 GHz</option>
	<option value="2">2 CPU - 4 GHz</option>
	<option value="4">4 CPU - 8 GHz</option>
</select>

<br/><br/>

<label>Memory resource</label>
<select name="memory">
	<option value="512">512 MB</option>
	<option value="1024">1024 MB</option>
	<option value="2048">2048 MB</option>
	<option value="4096">4096 MB</option>
</select>

<br/><br/>

<label>Virtual Machine Name</label>
<input name="vmname" id="vm"> </input>

<br/><br/>

<label>Operations</label>
<select name="operation">
	<option value="Create">Create</option>
	<option value="Start">Start/Deploy</option>
	<option value="Stop">Stop</option>
	<option value="Suspend">Suspend</option>
	<option value="Reset">Reset</option>
	<option value="Delete">Delete</option>
</select>

<br/><br/>

<input type="submit" name= "event" value="Go">
</form>
</div>


<div id="footer">

<label>Virtual Machine Inventory List</label>
<br/>
<label>Select virtual machine to deploy</label>
<g:select id="dep" from="${ls}" name="deploy" size="6" onchange="displayChange()"/>

</div>

</body>
</html>