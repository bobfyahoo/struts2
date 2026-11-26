<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Tickets</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
        }

        table {
            border-collapse: collapse;
            width: 100%;
            margin-top: 20px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
        }

        #message {
            margin-top: 15px;
        }
    </style>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>
<body>

    <h1>Tickets</h1>

    <div id="message">Loading tickets...</div>

    <table id="ticketsTable">
        <thead>
            <tr>
                <th>ID</th>
                <th>Summary</th>
                <th>Assignee</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody id="ticketsBody">
        </tbody>
    </table>

    <script>
        const apiUrl = '<%= request.getContextPath() %>/api/v3/tickets';

        function loadTickets() {
            $.ajax({
                url: apiUrl,
                method: 'GET',
                dataType: 'json',

                success: function(tickets) {
                    const tbody = $('#ticketsBody');
                    tbody.empty();

                    if (!Array.isArray(tickets)) {
                        $('#message').text('Unexpected response from server.');
                        return;
                    }

                    if (tickets.length === 0) {
                        $('#message').text('No tickets found.');
                        return;
                    }

                    tickets.forEach(function(ticket) {
                        const row = $('<tr>');

                        $('<td>').text(ticket.id).appendTo(row);
                        $('<td>').text(ticket.summary).appendTo(row);
                        $('<td>').text(ticket.assignee || '').appendTo(row);
                        $('<td>').text(ticket.status || '').appendTo(row);

                        tbody.append(row);
                    });

                    $('#message').text(
                        tickets.length + ' ticket(s) found.'
                    );
                },

                error: function(xhr) {
                    $('#message').text(
                        'Unable to load tickets. HTTP status: ' +
                        xhr.status
                    );
                }
            });
        }

        $(document).ready(function() {
            loadTickets();
        });
    </script>

</body>
</html>