<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>
	<title> Griffons - Welcome to our Cloud</title>
	<style type="text/css">
		.main{
			text-align: center;
			margin-top: 20px;
			margin-bottom: 20px;
			border-style: groove;
		}
		#left{
			margin-left: 100px;
			float:left;
			text-align: right;
		}
		#right{
			display: inline-block;
			text-align: left;
		}
		#left_center{
			margin-left: 100px;
			float:left;
			text-align: right;
			vertical-align: middle;
		}
		#footer
		{
			text-align: center;
			margin-top: 20px;
			margin-bottom: 20px;
			border-style: groove;
		}
		.lab
		{
			margin-top: 13px;
			margin-bottom: 13px;
		}
		.sp1
		{
			margin-top: 8px;
			margin-bottom: 10px;
		}
		.but
		{
			margin-top: 8px;
			margin-bottom: 8px;
			background: #ccc;
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
	<div class="title"> 
		<h1 align="center" ><font size="18"> Cloud Access </font></h1>
	</div>

	<div id="middle" class="main">
		<form name="VManagerUI" method="post" action="">

			<div id="left">
				<br/>
				<label class="lab" >Select Template</label>
				<br/>
				<label class="lab">CPU resource</label>
				<br/>
				<label class="lab">Memory resource</label>
				<br/>
				<label class="lab">Virtual Machine Name</label>
				<br/>
				<label class="lab">Operations</label>
				<br/>
			</div>

			<div id="right">
				<br/>
				<select name="temp" class="sp1">
					<option  value="griffons_ubuntu_12.04_desktop">Ubuntu 12.04</option>
					<option  value="griffons_linuxmint_14.1_cinnamon_desktop">Mint 14.1</option>
					<option  value="griffons_peppermint_3_20120722_desktop">PepperMint 3</option>				</select>
				<br/>
				<select name="cpu" class="sp1">
					<option value="1">1 CPU - 2 GHz</option>
					<option value="2">2 CPU - 4 GHz</option>
					<option value="4">4 CPU - 8 GHz</option>
				</select> 
				<br/>
				<select name="memory" class="sp1">
					<option value="512">512 MB</option>
					<option value="1024">1024 MB</option>
					<option value="2048">2048 MB</option>
					<option value="4096">4096 MB</option>
				</select> 
				<br/>
				<input name="vmname" id="vm" class="sp1"> 
				<br/>
				<select name="operation" class="sp1">
					<option value="Create">Create</option>
					<option value="Start">Start/Deploy</option>
					<option value="Stop">Stop</option>
					<option value="Suspend">Suspend</option>
					<option value="Reset">Reset</option>
					<option value="Delete">Delete</option>
				</select>
				<br/>
				<input type="submit" name= "event" class="but" value="Go"/>
				<br/>
			</div>
			
		</form>
	</div>
	
	<div id="footer">

		<br/>
		<h2>Virtual Machine Inventory List</h2>
		<br/>

		<div id="left">
			<label class="lab">Select virtual machine to deploy</label>
		</div>
	
		<div id="right">
			<g:select class="sp1" id="dep" from="${ls}" name="deploy" size="6" onchange="displayChange()"/>
		</div>
	</div>

</body>
</html>