/*
 * #%L
 * Cantharella :: Web
 * $Id: slidemenu.js 232 2013-05-28 10:15:32Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/webapp/js/slidemenu.js $
 * %%
 * Copyright (C) 2009 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
var slideMenu=function(){
	var sp,st,t,m,sa,l,w,sw,ot,ssl;
	return{
		build:function(sm,sw,mt,s,sl,h){
			sp=s; st=sw; t=mt;
			m=document.getElementById(sm);
			sa=m.children;
			l=sa.length; w=m.offsetWidth; sw=w/l;
			ot=Math.floor((w-st)/(l-1)); var i=0;
			for(i;i<l;i++){x=sa[i]; x.style.width=sw+'px'; this.timer(x)}
			if(sl!=null){ssl=sl;m.timer=setInterval(function(){slideMenu.slide(sa[sl-1])},t)}
		},
		timer:function(s){
			s.onmouseover=function(){clearInterval(m.timer);m.timer=setInterval(function(){slideMenu.slide(s)},t)};
			s.onmouseout=function(e){clearInterval(m.timer);m.timer=setInterval(function(){slideMenu.slide(sa[ssl-1])},t)};
		},
			
		slide:function(s){
			var cw=parseInt(s.style.width,'10');
			if(cw<st){
				var owt=0; var i=0;
				for(i;i<l;i++){
					if(sa[i]!=s){
						var o,ow; var oi=0; o=sa[i]; ow=parseInt(o.style.width,'10');
						if(ow>ot){oi=Math.floor((ow-ot)/sp); oi=(oi>0)?oi:1; o.style.width=(ow-oi)+'px'}
						owt=owt+(ow-oi)}}
				s.style.width=(w-owt)+'px';
			}else{clearInterval(m.timer)}
		}
	};
}();