package br.com.entulhosParanhana.controller;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import br.com.entulhosParanhana.dao.AcessoUsuarioDao;
import br.com.entulhosParanhana.dao.UsuarioDao;
import br.com.entulhosParanhana.model.AcessoUsuario;
import br.com.entulhosParanhana.model.Usuario;
import br.com.entulhosParanhana.uteis.Uteis;

@Named(value="usuarioController")
@SessionScoped
public class UsuarioManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Usuario usuarioLogin;
	private Usuario usuarioLogado;
	
	public UsuarioManagedBean() {
		this.usuarioLogin = new Usuario();
		this.usuarioLogado = new Usuario();
	}

	public Usuario GetUsuarioSession() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return (Usuario) facesContext.getExternalContext().getSessionMap().get("usuarioAutenticado");
	}

	public String Logout() {
		
		acessoUsuario(UsuarioDao.getInstance().getById(usuarioLogado.getId()),"Logout");		
		
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "/index.xhtml?faces-redirect=true";
	}

	public String EfetuarLogin() {
		
		if(StringUtils.isEmpty(usuarioLogin.getCpf()) || StringUtils.isBlank(usuarioLogin.getCpf())){
			Uteis.Mensagem("Favor informar CPF!");
			return null;
		} else if (StringUtils.isEmpty(usuarioLogin.getSenha()) || StringUtils.isBlank(usuarioLogin.getSenha())) {
			Uteis.Mensagem("Favor informar senha!");
			return null;
		} else {
			Usuario usuarioTemp = UsuarioDao.getInstance().findByCpfSenha(usuarioLogin);
			
			if (usuarioTemp == null) {
				Uteis.MensagemAtencao("N�o foi poss�vel efetuar login. Verifique suas cred�nciais!");
				return null;
			}else if(!usuarioTemp.isStatus()){
				Uteis.MensagemAtencao("N�o foi poss�vel efetuar login. Usu�rio bloqueado!");
				return null;
			}else {
				this.usuarioLogado  = new Usuario();
				usuarioLogado.setSenha(null);
				usuarioLogado.setId(usuarioTemp.getId());
				usuarioLogado.setNome(usuarioTemp.getNome());
				usuarioLogado.setCpf(usuarioTemp.getCpf());
				usuarioLogado.setPerfil(usuarioTemp.getPerfil());
				acessoUsuario(usuarioTemp,"Login");	
				
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.getExternalContext().getSessionMap().put("usuarioAutenticado", usuarioLogado);
				
				return "sistema/home?faces-redirect=true";
			}
		}
	}

	/**Metodo Respons�vel por salvar o tipo de acesso (login, logout) na base*///
	public void acessoUsuario(Usuario usuarioTemp, String tipo) {
		AcessoUsuario acessoUsuario = new AcessoUsuario();
		acessoUsuario.setUsuario(usuarioTemp);
		acessoUsuario.setTipo(tipo);	
		acessoUsuario.setData( new Date());
		AcessoUsuarioDao.getInstance().merge(acessoUsuario);
	}
	
	public Usuario getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(Usuario usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	public Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(Usuario usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}	
	
}
