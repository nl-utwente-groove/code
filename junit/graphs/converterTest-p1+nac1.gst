<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n92"/>
        <node id="n91"/>
        <node id="n89"/>
        <node id="n90"/>
        <edge from="n92" to="n92">
            <attr name="label">
                <string>new</string>
            </attr>
        </edge>
        <edge from="n89" to="n91">
            <attr name="label">
                <string>not:c</string>
            </attr>
        </edge>
        <edge from="n90" to="n92">
            <attr name="label">
                <string>new:b</string>
            </attr>
        </edge>
        <edge from="n89" to="n89">
            <attr name="label">
                <string>del</string>
            </attr>
        </edge>
        <edge from="n91" to="n91">
            <attr name="label">
                <string>not:</string>
            </attr>
        </edge>
        <edge from="n90" to="n90">
            <attr name="label">
                <string>use</string>
            </attr>
        </edge>
        <edge from="n90" to="n89">
            <attr name="label">
                <string>del:a</string>
            </attr>
        </edge>
    </graph>
</gxl>
