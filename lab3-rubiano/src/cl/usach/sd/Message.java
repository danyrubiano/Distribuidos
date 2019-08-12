package cl.usach.sd;

import java.util.ArrayList;

/**
 * Clase la cual vamos a utilizar para enviar datos de un Peer a otro
 */
public class Message {
	private int consulta;
	// Guarda el emisor de la consulta
	private int emisor;
	// Guarda la IP del super peer de destino
	private int spDestino;
	// Identifica si va de ida o de vuelta, 1: Ida, 0: Vuelta
	private int idaVuelta; 
	// Guarda la respuesta
	private int respuesta;
	// Lista para guardar el camino
	private ArrayList<Integer> Camino;
	// guarda la posicion que va seteando en el camino, para luego retornar la respuesta
	private int contador;
	// guarda el ttl del mensaje
	private int ttl;
	// guarda el k del random walks
	private int k;
	// guarda el tipo de k random walks
	// - Si es 0, no hay k random walks
	// - si es 1, hay un k random walks para la busqueda del super peer de la subred
	// - Si es 2, hay un k random walks para la busqueda del peer que tenga la consulta
	private int kwr;

	public Message(int emisor, int consulta, int spDestino, int idaVuelta, int respuesta, ArrayList<Integer> Camino, int contador, int ttl, int k, int kwr) {

		this.setConsulta(consulta);
		this.setEmisor(emisor);
		this.setSpDestino(spDestino);
		this.setCamino(Camino);
		this.setIdaVuelta(idaVuelta);
		this.setRespuesta(respuesta);
		this.setContador(contador);
		this.setTtl(ttl);
		this.setK(k);
		this.setKwr(kwr);
	}

	public int getEmisor() {
		return emisor;
	}

	public void setEmisor(int emisor) {
		this.emisor = emisor;
	}

	public int getConsulta() {
		return consulta;
	}

	public void setConsulta(int consulta) {
		this.consulta = consulta;
	}
	
	public int getSpDestino() {
		return spDestino;
	}

	public void setSpDestino(int spDestino) {
		this.spDestino = spDestino;
	}
	
	public ArrayList<Integer> getCamino() {
		return Camino;
	}

	public void setCamino(ArrayList<Integer> Camino) {
		this.Camino = Camino;
	}
	
	public int getIdaVuelta() {
		return idaVuelta;
	}

	public void setIdaVuelta(int idaVuelta) {
		this.idaVuelta = idaVuelta;
	}
	
	public int getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(int respuesta) {
		this.respuesta = respuesta;
	}
	
	public int getContador() {
		return contador;
	}

	public void setContador(int contador) {
		this.contador = contador;
	}
	
	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	
	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}
	
	public int getKwr() {
		return kwr;
	}

	public void setKwr(int kwr) {
		this.kwr = kwr;
	}
}
