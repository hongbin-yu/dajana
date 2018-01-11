<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="authz" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"  %>

<div class="wb-tabs carousel-s2 playing slow">
	<ul role="tablist">
	<c:forEach items="${assets.items }" var="item" varStatus="loop">
		<c:if test="${loop.count==1}">
			<li class="active">
				<a href="#tab${loop.count}">
					<img class="img-responsive" src="<c:url value='viewimage?uid=${item.uid}'></c:url>" alt="${item.alt}" />
					<span class="wb-inv">Tab ${loop.count}:${item.title}</span>
				</a>
			</li>
		</c:if>
		<c:if test="${loop.count>1}">
			<li>
				<a href="#tab${loop.count}">
					<img class="img-responsive" src="<c:url value='viewimage?uid=${item.uid}'></c:url>" alt="${item.alt}" />
					<span class="wb-inv">Tab ${loop.count}:${item.title}</span>
				</a>
			</li>
		</c:if>		
	</c:forEach>
	</ul>
	<div class="tabpanels">
		<c:forEach items="${assets.items }" var="item" varStatus="loop">
			<c:if test="${loop.count==1}">
				<div role="tabpanel" id="tab${loop.count}" class="fade in">
					<!-- first child - tabpanel -start -->
						<figure>
							<a href="${item.url}" class="learnmore">
								<img class="img-responsive" src="<c:url value='viewimage?uid=${item.uid}'></c:url>" alt="${item.alt}" />
							</a>
						<figcaption>
							   <a href="${item.url}" class="learnmore">${item.title}</a>
								${item.description}
							</figcaption>
						</figure>
				</div>
			</c:if>	
			<c:if test="${loop.count>1}">
				<div role="tabpanel" id="tab${loop.count}" class="fade out">
					<!-- first child - tabpanel -start -->
						<figure>
					<a href="${item.url}" class="learnmore">
								<img class="img-responsive" src="<c:url value='viewimage?uid=${item.uid}'></c:url>" alt="${item.alt}" />
					</a>
							<figcaption>
							    <a href="${item.url}" class="learnmore">${item.title}</a>
								${item.description}
							</figcaption>
						</figure>
				</div>
			</c:if>	
		</c:forEach>
	</div>
</div>

