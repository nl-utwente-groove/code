<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="mergeErased" role="graph" edgeids="false" edgemode="directed">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n138212"/>
        <node id="n138213"/>
        <node id="n138210"/>
        <node id="n138211"/>
        <node id="n138209"/>
        <edge from="n138213" to="n138213">
            <attr name="label">
                <string>rem:</string>
            </attr>
        </edge>
        <edge from="n138209" to="n138209">
            <attr name="label">
                <string>rem:</string>
            </attr>
        </edge>
        <edge from="n138213" to="n138213">
            <attr name="label">
                <string>Two nodes, which will be matched by both the top level A and the sublevel A</string>
            </attr>
        </edge>
        <edge from="n138210" to="n138210">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge from="n138213" to="n138210">
            <attr name="label">
                <string>A-node</string>
            </attr>
        </edge>
        <edge from="n138212" to="n138212">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge from="n138209" to="n138211">
            <attr name="label">
                <string>merged node</string>
            </attr>
        </edge>
        <edge from="n138211" to="n138211">
            <attr name="label">
                <string>B</string>
            </attr>
        </edge>
        <edge from="n138209" to="n138209">
            <attr name="label">
                <string>The node to be merged with A on the sublevel (and therefore deleted)</string>
            </attr>
        </edge>
        <edge from="n138213" to="n138212">
            <attr name="label">
                <string>A-node</string>
            </attr>
        </edge>
    </graph>
</gxl>
