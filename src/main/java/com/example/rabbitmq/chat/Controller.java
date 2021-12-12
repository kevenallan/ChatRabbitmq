package com.example.rabbitmq.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

@RestController
public class Controller {

	private List<String> msgs = new ArrayList<>();
	
	
	@PostMapping(value = "/enviar")
	public void enviar(@RequestParam String msg) throws Exception {
		this.armazenarMsg();
		//Criacao de uma factory de conexao, responsavel por criar as conexoes
        ConnectionFactory connectionFactory = new ConnectionFactory();

        //localizacao do gestor da fila (Queue Manager)
        connectionFactory.setHost("jackal-01.rmq.cloudamqp.com");
        connectionFactory.setUsername("evjqwqns");
        connectionFactory.setPassword("QySYSyXnfenHtCmsRTzWY26GhOTyAosT");
		connectionFactory.setVirtualHost("evjqwqns");
        
//        connectionFactory.setPort(5672);

        String NOME_FILA = "filaOla";
        try(
            //criacao de uma coneccao
            Connection connection = connectionFactory.newConnection();
            //criando um canal na conexao
            Channel channel = connection.createChannel()) {
        	//Esse corpo especifica o envio da mensagem para a fila

            //Declaracao da fila. Se nao existir ainda no queue manager, serah criada. Se jah existir, e foi criada com
            // os mesmos parametros, pega a referencia da fila. Se foi criada com parametros diferentes, lanca excecao
            channel.queueDeclare(NOME_FILA, false, false, false, null);
            String mensagem = "Olá mundo!";
            //publica uma mensagem na fila
            channel.basicPublish("", NOME_FILA, null, msg.getBytes());
            System.out.println("Enviei mensagem: " + msg);
        }
	}
	

	@GetMapping(value = "/armazenar")
	public void armazenarMsg() throws Exception{
		 String NOME_FILA = "filaOla";

	        //criando a fabrica de conexoes e criando uma conexao
	        ConnectionFactory connectionFactory = new ConnectionFactory();
		   connectionFactory.setHost("jackal-01.rmq.cloudamqp.com");
	        connectionFactory.setUsername("evjqwqns");
	        connectionFactory.setPassword("QySYSyXnfenHtCmsRTzWY26GhOTyAosT");
			connectionFactory.setVirtualHost("evjqwqns");

//	        connectionFactory.setHost("localhost");
	        Connection conexao = connectionFactory.newConnection();

	        //criando um canal e declarando uma fila
	        Channel canal = conexao.createChannel();
	        canal.queueDeclare(NOME_FILA, false, false, false, null);

	        //Definindo a funcao callback
	        DeliverCallback callback = (consumerTag, delivery) -> {
	            String mensagem = new String(delivery.getBody());
	        	this.x(mensagem);
	            System.out.println("Recebi a mensagem: " + mensagem);
	           
	        };

	        //Consome da fila
	        canal.basicConsume(NOME_FILA, true, callback, consumerTag -> {
//	        	System.out.println("========");
//	        	System.out.println(callback.toString());

	        	
	        	
	        });
	        
	        System.out.println("Continuarei executando outras atividades enquanto não chega mensagem...");
	};
	
	@GetMapping(value = "/receber")
	public List<String> receber() {
		return this.msgs;
	};
	
	
	public void x (String x) {
		this.msgs.add(x);
		System.out.println(this.msgs);
		System.out.println("msg adicionada");
	}
}
