<!doctype html>
<%@page import="entity.Channel"%>
<%@page import="java.util.ArrayList"%>
<html lang="en">

<head>
<!-- Required meta tags -->
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<!-- Bootstrap CSS -->
<link rel="stylesheet" href="bootstrap.css">

<title>Channels</title>
<style>
</style>
</head>

<body>
	<div class="jumbotron">
		<h1 class="display-3">Channels</h1>
		<br>
		<form name="RegisterForm" method="post" action="ShowChannels">
		</form>
		<table id="myTable" class="table table-hover">
			<thead>
				<tr>
					<th scope="col">Address</th>
					<th scope="col">Balance</th>
					<th scope="col">Deposit</th>
					<th scope="col">Close Channel</th>
				</tr>
			</thead>
			<tbody>
				<%
					int count = 0;
					ArrayList<Channel> channels = null;
					try {
						count = (int) request.getAttribute("count");
						channels = (ArrayList<Channel>) request.getAttribute("channels");

					} catch (Throwable t) {
					}
				%>
				<%
					for (int i = 0; i < count; i++) {
				%>
				<tr class="table-active">
					<th scope="row"><%=channels.get(i).destination_address%></th>
					<td><%=channels.get(i).balance + " ETH"%></td>
					<td>
						<form action="deposit_to_channel.html">
							<%
				        	session.setAttribute("tableIndex", i);
							%>
							<input class="btn btn-outline-primary" type="submit"
								value="Deposit" />
						</form>
					</td>
					<td>
						<form action="CloseChannel" method="post">
						
							<input class="btn btn-outline-primary" type="submit" value="Close Channel">
						</form>
					</td>
				</tr>
				<%
					}
				%>
			</tbody>
		</table>
		<br>
		<div>
			<form action="menu.html">
				<input class="btn btn-outline-adir" type="submit" value="Main Menu" />
			</form>
		</div>
	</div>
	<script>
		function DeleteRowFunction(o) {
			//no clue what to put here?
			var p = o.parentNode.parentNode;
			p.parentNode.removeChild(p);
		}
	</script>
</body>

</html>