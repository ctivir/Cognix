$(function() {

    $('input:text').addClass("ui-corner-all");

    $('.openModal').click(function() {
        $('#modalContent').load($(this).attr('id'), function() {
            $('#mainModal').modal();
        });

    });

    /*Dialogo de confirmacao*/
    $(document).on("click", ".confirmLink", function(e) {
        e.preventDefault();
        $('#actionModalConfirm')
                .attr('href', $(this).attr('href'))
                .attr('idObj', $(this).attr('id'));

        $("#msgApagar").text($(this).attr('title'));
        $("#confirmModal").modal();
    });


    $(document).on("click", "#actionModalConfirm", function(e) {
        e.preventDefault();
        _thisButton = $(this);

        $.post($(this).attr('href'), "", function(resultado) {
            if (resultado["type"]) {
                var type = unescape(resultado["type"]);
                if (type === "success") {
                    var container = $('#container' + _thisButton.attr('idObj'));
                    $('#confirmModal').modal('hide');
                    container.fadeOut(1000, function() {
                        container.remove();
                    });
                } else {
                    $('#confirmModal').modal('hide');
                    $("#textStatus").text(type);
                    $("#errorThrown").text(unescape(resultado["message"]));
                    if (type === 'warn') {
                        $("#error-type").text("Warn: ");
                    }
                    $("#modalMsg").modal();
                }
            } else {
                $('#confirmModal').modal('hide');
                $("#errorThrown").text("Erro! Não foi possível executar a operação!");
                $("#modalMsg").modal();
            }
        }, "json")
                .error(function() {
                    $('#confirmModal').modal('hide');
                    $("#errorThrown").text("Erro! Não foi possível executar a operação!");
                    $("#modalMsg").modal();
                });

        //todo: colocar aqui um gif loading
    });

});
