
public class NodoReturn extends NodoSentencia{

	//atributo nodo expresion
	private NodoExpresion expresion;
	private String metodo; //metodo al q pertenece
	private Clase clase;
	private NodoBloque bq;
	
	public NodoReturn(NodoExpresion e, String m, Token t, Clase c, NodoBloque b){
		super(t);
		expresion=e;
		metodo=m;
		setClase(c);
		bq=b;
	}
	
	public NodoExpresion getExpresion() {
		return expresion;
	}

	public void setExpresion(NodoExpresion expresion) {
		this.expresion = expresion;
	}
	
	public void chequear() throws Exception{
		if (clase.getNombre().equals(metodo))
			throw new Exception("Error, la sentencia return no puede existir en un constructor. Linea "+token.getNroLinea());
			
		TipoMetodo tipoMetodo = clase.getMetodo(metodo).getTipo();
		
		if (tipoMetodo.getTipo().equals("void")){
			if (expresion != null)
				throw new Exception("Error, return no puede existir en un metodo 'void'. Linea "+token.getNroLinea());
		}
		else{
			if (expresion == null)
				throw new Exception("Error, el tipo de retorno del metodo debe ser 'void' si se utiliza 'return ;'. Linea "+token.getNroLinea());
			else {
				TipoMetodo t = expresion.chequear();
			//	System.out.println("la expresion es tipo "+expresion.getToken().getLexema());
				
				if (!t.conformidad(tipoMetodo))
					throw new Exception("Error, el tipo de de retorno debe ser "+tipoMetodo.getTipo()+". Linea "+token.getNroLinea());
			}
		}
		
		clase.getMetodo(metodo).tieneReturn();
			
	}

	public String getMetodo() {
		return metodo;
	}

	public void setMetodo(String metodo) {
		this.metodo = metodo;
	}

	public Clase getClase() {
		return clase;
	}

	public void setClase(Clase clase) {
		this.clase = clase;
	}

	@Override
	public void generar(TablaDeSimbolos t) {
		Metodo metaux = clase.getMetodo(metodo);
		int offset;
		
		if (expresion != null) { //si no es return; 
			expresion.generar(t);
			offset = metaux.getParametros().size() + 3;
			
			if (metaux.getFormaMetodo().equals("dynamic")) //aumento por this
				offset++;
			
			t.generarInstruccion("STORE "+offset);		
		}
			
		t.generarInstruccion("FMEM "+ bq.getVariablesLocales().size());
		t.generarInstruccion("STOREFP ");
		
		if(metaux.getFormaMetodo().equals("static"))
			t.generarInstruccion("RET "+ metaux.getParametros().size());
		else
			t.generarInstruccion("RET "+ (metaux.getParametros().size() + 1));
		
		
	}
		
	
}
